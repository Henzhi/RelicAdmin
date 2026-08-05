package com.relic.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据库 SQL 导出工具（流式读取，避免 OOM）
 *
 * <p>整改背景：原导出逻辑使用 {@code SELECT *} 将整表数据一次性加载到内存，
 * 行为日志等大表（百万级行）导出时可能导致 OOM。</p>
 *
 * <p>整改方案：使用 MySQL Connector/J 的流式读取模式
 * （{@code TYPE_FORWARD_ONLY + CONCUR_READ_ONLY + setFetchSize(Integer.MIN_VALUE)}），
 * 服务端逐行返回数据，客户端边读边写、每 500 行落盘一个 INSERT 批次，
 * 内存占用与表大小无关，仅与单批大小相关。</p>
 *
 * <p>注意：流式 ResultSet 未读完前，同一 Connection 不能执行其他语句，
 * 因此本工具内部按「每张表：先结构后数据、数据读完全部行后再进入下一张表」的顺序处理。</p>
 */
@Slf4j
public final class SqlExportUtil {

    /** 单批 INSERT 行数 */
    public static final int BATCH_SIZE = 500;

    /** 无需导出的表（Flyway 版本表由迁移工具管理） */
    private static final Set<String> EXCLUDE_TABLES = new HashSet<>(Arrays.asList("flyway_schema_history"));

    private SqlExportUtil() {
    }

    /**
     * 导出全部业务表结构与数据到 Writer（流式）
     *
     * @param conn         数据源连接（由调用方管理生命周期）
     * @param writer       输出流（由调用方管理生命周期）
     * @param headerComment 文件头注释（如备份/应急备份标识）
     * @return 导出的表数量
     * @throws SQLException 数据库访问异常
     * @throws IOException  写入异常
     */
    public static int exportAllTables(Connection conn, Writer writer, String headerComment) throws SQLException, IOException {
        if (headerComment != null) {
            writer.write(headerComment);
            writer.write("\n");
        }
        writer.write("-- 数据库: seitem\n");
        writer.write("SET NAMES utf8mb4;\n");
        writer.write("SET FOREIGN_KEY_CHECKS = 0;\n\n");

        List<String> tables = listTables(conn);

        for (String table : tables) {
            exportTableStructure(conn, writer, table);
            exportTableData(conn, writer, table);
        }

        writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
        writer.flush();
        log.info("SQL 导出完成（流式），共 {} 张表", tables.size());
        return tables.size();
    }

    private static List<String> listTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                String table = rs.getString(1);
                if (!EXCLUDE_TABLES.contains(table)) {
                    tables.add(table);
                }
            }
        }
        return tables;
    }

    private static void exportTableStructure(Connection conn, Writer writer, String table) throws SQLException, IOException {
        writer.write("-- ----------------------------\n");
        writer.write("-- Table structure for " + table + "\n");
        writer.write("-- ----------------------------\n");
        writer.write("DROP TABLE IF EXISTS `" + table + "`;\n");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW CREATE TABLE `" + table + "`")) {
            if (rs.next()) {
                writer.write(rs.getString(2) + ";\n");
            }
        }
        writer.write("\n");
    }

    /**
     * 流式读取整表数据并分批写出
     */
    private static void exportTableData(Connection conn, Writer writer, String table) throws SQLException, IOException {
        // MySQL 流式读取：TYPE_FORWARD_ONLY + CONCUR_READ_ONLY + fetchSize=Integer.MIN_VALUE
        try (Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            st.setFetchSize(Integer.MIN_VALUE);
            try (ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`")) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                List<String> columns = new ArrayList<>(colCount);
                int[] columnTypes = new int[colCount];
                for (int i = 1; i <= colCount; i++) {
                    columns.add(meta.getColumnName(i));
                    columnTypes[i - 1] = meta.getColumnType(i);
                }

                StringBuilder batch = new StringBuilder(4096);
                int rowInBatch = 0;
                boolean anyRow = false;

                while (rs.next()) {
                    if (rowInBatch == 0) {
                        batch.setLength(0);
                        batch.append("INSERT INTO `").append(table).append("` (");
                        for (int i = 0; i < colCount; i++) {
                            if (i > 0) batch.append(", ");
                            batch.append('`').append(columns.get(i)).append('`');
                        }
                        batch.append(") VALUES\n");
                    } else {
                        batch.append(",\n");
                    }
                    batch.append("  (");
                    for (int i = 0; i < colCount; i++) {
                        if (i > 0) batch.append(", ");
                        appendValue(batch, rs, i + 1, columnTypes[i]);
                    }
                    batch.append(")");

                    rowInBatch++;
                    anyRow = true;

                    if (rowInBatch >= BATCH_SIZE) {
                        batch.append(";\n");
                        writer.write(batch.toString());
                        rowInBatch = 0;
                    }
                }

                if (rowInBatch > 0) {
                    batch.append(";\n");
                    writer.write(batch.toString());
                }
                if (anyRow) {
                    writer.write("\n");
                }
            }
        }
    }

    private static void appendValue(StringBuilder sb, ResultSet rs, int columnIndex, int sqlType) throws SQLException {
        String val = rs.getString(columnIndex);
        if (val == null) {
            sb.append("NULL");
        } else if (isStringType(sqlType)) {
            String escaped = val.replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
            sb.append('\'').append(escaped).append('\'');
        } else {
            sb.append(val);
        }
    }

    private static boolean isStringType(int sqlType) {
        switch (sqlType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.NVARCHAR:
            case Types.NCHAR:
            case Types.LONGNVARCHAR:
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return true;
            default:
                return false;
        }
    }
}
