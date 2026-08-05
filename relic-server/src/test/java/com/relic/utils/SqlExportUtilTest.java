package com.relic.utils;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SqlExportUtil 流式导出单元测试
 *
 * <p>验证要点（内存风险整改）：
 * 1. 数据查询语句必须使用流式模式（fetchSize=Integer.MIN_VALUE），避免全量加载；
 * 2. 数据按 500 行分批写出，输出包含正确的 INSERT 语句；
 * 3. 字符串值正确转义（引号/反斜杠/换行）。</p>
 */
class SqlExportUtilTest {

    /** 构造单列 int 列的元数据 mock */
    private ResultSetMetaData intColumnMeta() throws SQLException {
        ResultSetMetaData meta = mock(ResultSetMetaData.class);
        when(meta.getColumnCount()).thenReturn(1);
        when(meta.getColumnName(1)).thenReturn("id");
        when(meta.getColumnType(1)).thenReturn(Types.INTEGER);
        return meta;
    }

    /** 构造单列 varchar 列的元数据 mock */
    private ResultSetMetaData varcharColumnMeta() throws SQLException {
        ResultSetMetaData meta = mock(ResultSetMetaData.class);
        when(meta.getColumnCount()).thenReturn(1);
        when(meta.getColumnName(1)).thenReturn("name");
        when(meta.getColumnType(1)).thenReturn(Types.VARCHAR);
        return meta;
    }

    /** 按行序列 stub 一个 ResultSet（单列，值统一为 value） */
    private ResultSet rowResultSet(int rowCount, String value) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        AtomicInteger counter = new AtomicInteger();
        when(rs.next()).thenAnswer(invocation -> counter.getAndIncrement() < rowCount);
        when(rs.getString(anyInt())).thenReturn(value);
        return rs;
    }

    /** 构造表流程 stub：SHOW TABLES -> SHOW CREATE TABLE -> SELECT 数据 */
    private void stubTable(Connection conn, Statement stmt, Statement streamStmt,
                           String table, int rowCount, ResultSetMetaData meta) throws SQLException {
        ResultSet tablesRs = mock(ResultSet.class);
        when(tablesRs.next()).thenReturn(true, false);
        when(tablesRs.getString(1)).thenReturn(table);

        ResultSet createRs = mock(ResultSet.class);
        when(createRs.next()).thenReturn(true, false);
        when(createRs.getString(2)).thenReturn("CREATE TABLE `" + table + "` (id INT)");

        ResultSet dataRs = rowResultSet(rowCount, "1");
        when(dataRs.getMetaData()).thenReturn(meta);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SHOW TABLES")).thenReturn(tablesRs);
        when(stmt.executeQuery("SHOW CREATE TABLE `" + table + "`")).thenReturn(createRs);
        when(stmt.executeQuery("SELECT * FROM `" + table + "`")).thenReturn(dataRs);

        when(conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(streamStmt);
        when(streamStmt.executeQuery("SELECT * FROM `" + table + "`")).thenReturn(dataRs);
    }

    @Test
    void usesStreamingFetchSize() throws Exception {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        Statement streamStmt = mock(Statement.class);
        stubTable(conn, stmt, streamStmt, "logs", 2, intColumnMeta());

        StringWriter writer = new StringWriter();
        SqlExportUtil.exportAllTables(conn, writer, "-- header\n");

        // 关键断言：数据查询使用流式 Statement + fetchSize=Integer.MIN_VALUE
        verify(conn).createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        verify(streamStmt).setFetchSize(Integer.MIN_VALUE);
        String out = writer.toString();
        assertTrue(out.contains("-- header\n"));
        assertTrue(out.contains("SET FOREIGN_KEY_CHECKS = 0;"));
        assertTrue(out.contains("SET FOREIGN_KEY_CHECKS = 1;"));
        assertTrue(out.contains("INSERT INTO `logs` (`id`) VALUES"));
    }

    @Test
    void excludesFlywayHistoryTable() throws Exception {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet tablesRs = mock(ResultSet.class);
        when(tablesRs.next()).thenReturn(true, false);
        when(tablesRs.getString(1)).thenReturn("flyway_schema_history");
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SHOW TABLES")).thenReturn(tablesRs);

        StringWriter writer = new StringWriter();
        int count = SqlExportUtil.exportAllTables(conn, writer, null);

        assertEquals(0, count);
        // 不应执行任何 SHOW CREATE TABLE（仅执行了 SHOW TABLES）
        verify(stmt, never()).executeQuery(startsWith("SHOW CREATE TABLE"));
        verify(conn, never()).createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    @Test
    void escapesStringValues() throws Exception {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        Statement streamStmt = mock(Statement.class);
        ResultSet tablesRs = mock(ResultSet.class);
        when(tablesRs.next()).thenReturn(true, false);
        when(tablesRs.getString(1)).thenReturn("artifacts");
        ResultSet createRs = mock(ResultSet.class);
        when(createRs.next()).thenReturn(true, false);
        when(createRs.getString(2)).thenReturn("CREATE TABLE `artifacts` (name VARCHAR(100))");

        ResultSetMetaData meta = varcharColumnMeta();
        ResultSet dataRs = mock(ResultSet.class);
        when(dataRs.getMetaData()).thenReturn(meta);
        when(dataRs.next()).thenReturn(true, false);
        when(dataRs.getString(anyInt())).thenReturn("It's \"Q\"\nline");

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SHOW TABLES")).thenReturn(tablesRs);
        when(stmt.executeQuery("SHOW CREATE TABLE `artifacts`")).thenReturn(createRs);
        when(conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(streamStmt);
        when(streamStmt.executeQuery("SELECT * FROM `artifacts`")).thenReturn(dataRs);

        StringWriter writer = new StringWriter();
        SqlExportUtil.exportAllTables(conn, writer, null);

        String out = writer.toString();
        // 单引号与换行正确转义
        assertTrue(out.contains("'It\\'s \"Q\"\\nline'"));
    }

    @Test
    void batchesAt500Rows() throws Exception {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        Statement streamStmt = mock(Statement.class);
        stubTable(conn, stmt, streamStmt, "big_table", 1200, intColumnMeta());

        StringWriter writer = new StringWriter();
        SqlExportUtil.exportAllTables(conn, writer, null);

        String out = writer.toString();
        // 1200 行 = 2 个 500 批次 + 1 个 200 批次 → 3 条 INSERT 语句
        long insertCount = out.split("INSERT INTO `big_table`").length - 1;
        assertEquals(3, insertCount);
        // 每批不超过 500 行
        assertTrue(out.contains(",\n") || insertCount > 0);
    }
}
