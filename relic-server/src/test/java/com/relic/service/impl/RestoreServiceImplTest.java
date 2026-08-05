package com.relic.service.impl;

import com.relic.mapper.BackupRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 恢复功能单元测试（M-16：DML 批量执行 + DDL 单独处理 + 如实回滚语义；
 * M-17：应急备份登记入库与失败返回应急备份 ID）
 *
 * <p>验证要点：
 * 1. INSERT（DML）走 addBatch/executeBatch，不逐条 execute；
 * 2. DROP/CREATE（DDL）单独 execute，且先 flush 已有 batch；
 * 3. 失败语句后不再执行剩余 batch，抛出含"应急备份"提示的异常。</p>
 */
class RestoreServiceImplTest {

    /** 测试注入 mock 依赖的最小实例（executeSqlFile 只依赖 dataSource） */
    private RestoreServiceImpl buildService(DataSource dataSource) {
        return new RestoreServiceImpl(
                null, null, null, null, dataSource, null, null);
    }

    /** 构造含全部 mock 依赖的实例（供 restore 全流程测试） */
    private RestoreServiceImpl buildFullService(BackupRecordMapper backupRecordMapper,
                                                DataSource dataSource) {
        return new RestoreServiceImpl(
                null, backupRecordMapper, null, null, dataSource, null, null);
    }

    private void writeSql(Path file, String content) throws IOException {
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void dmlUsesBatch_ddlExecutesSeparately(@TempDir Path dir) throws Exception {
        File sqlFile = dir.resolve("backup.sql").toFile();
        writeSql(sqlFile.toPath(),
                "SET NAMES utf8mb4;\n"
                        + "DROP TABLE IF EXISTS `a`;\n"
                        + "CREATE TABLE `a` (id INT);\n"
                        + "INSERT INTO `a` (id) VALUES (1);\n"
                        + "INSERT INTO `a` (id) VALUES (2);\n"
                        + "INSERT INTO `a` (id) VALUES (3);\n");

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        when(conn.createStatement()).thenReturn(st);
        when(st.executeBatch()).thenReturn(new int[]{1, 1, 1});
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(conn);

        RestoreServiceImpl service = buildService(dataSource);
        long count = service.executeSqlFile(sqlFile);

        assertEquals(6, count); // SET + DROP + CREATE + 3 INSERT
        InOrder inOrder = inOrder(st);
        // DDL 单独 execute（DROP、CREATE）
        inOrder.verify(st).execute("DROP TABLE IF EXISTS `a`");
        inOrder.verify(st).execute("CREATE TABLE `a` (id INT)");
        // 3 条 INSERT 走 batch
        inOrder.verify(st).addBatch("INSERT INTO `a` (id) VALUES (1)");
        inOrder.verify(st).addBatch("INSERT INTO `a` (id) VALUES (2)");
        inOrder.verify(st).addBatch("INSERT INTO `a` (id) VALUES (3)");
        inOrder.verify(st).executeBatch();
        // 除 2 条 DDL 外，INSERT 未单独 execute（全部走 batch）
        verify(st, never()).execute(startsWith("INSERT"));
        verify(conn).commit();
    }

    @Test
    void ddlFlushesPendingBatchBeforeExecute(@TempDir Path dir) throws Exception {
        File sqlFile = dir.resolve("backup.sql").toFile();
        writeSql(sqlFile.toPath(),
                "INSERT INTO `a` (id) VALUES (1);\n"
                        + "DROP TABLE `a`;\n");

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        when(conn.createStatement()).thenReturn(st);
        when(st.executeBatch()).thenReturn(new int[]{1});
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(conn);

        RestoreServiceImpl service = buildService(dataSource);
        service.executeSqlFile(sqlFile);

        InOrder inOrder = inOrder(st);
        inOrder.verify(st).addBatch("INSERT INTO `a` (id) VALUES (1)");
        // DDL 前先 flush 已累积的 INSERT batch
        inOrder.verify(st).executeBatch();
        inOrder.verify(st).execute("DROP TABLE `a`");
    }

    @Test
    void failure_rollsBack_andMentionsEmergencyBackup(@TempDir Path dir) throws Exception {
        File sqlFile = dir.resolve("backup.sql").toFile();
        writeSql(sqlFile.toPath(),
                "INSERT INTO `a` (id) VALUES (1);\n"
                        + "INSERT INTO `a` (id) VALUES (2);\n");

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        when(conn.createStatement()).thenReturn(st);
        when(st.executeBatch()).thenThrow(new SQLException("模拟插入失败"));
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(conn);

        RestoreServiceImpl service = buildService(dataSource);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.executeSqlFile(sqlFile));
        assertTrue(ex.getMessage().contains("应急备份"), "失败提示应包含应急备份指引");
        verify(conn).rollback();
        verify(conn, never()).commit();
    }

    @Test
    void failureStatements_clearBatch_skipRemaining(@TempDir Path dir) throws Exception {
        File sqlFile = dir.resolve("backup.sql").toFile();
        writeSql(sqlFile.toPath(),
                "INSERT INTO `a` (id) VALUES (1);\n"
                        + "DROP TABLE `b`;\n"
                        + "INSERT INTO `a` (id) VALUES (2);\n");

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        when(conn.createStatement()).thenReturn(st);
        when(st.executeBatch()).thenReturn(new int[]{1});
        doThrow(new SQLException("DROP 失败")).when(st).execute("DROP TABLE `b`");
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(conn);

        RestoreServiceImpl service = buildService(dataSource);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.executeSqlFile(sqlFile));
        assertTrue(ex.getMessage().contains("1 条语句执行失败"));
        verify(conn).rollback();
    }

    /**
     * M-17：恢复失败时，系统已登记的应急备份记录应可被查询到，
     * 且 scope 标记源备份 ID（前端据此提供一键还原）。
     */
    @Test
    void emergencyBackup_registeredWithSourceScope() throws Exception {
        BackupRecordMapper backupRecordMapper = mock(BackupRecordMapper.class);

        // 模拟应急备份查询：返回一条 scope 含 sourceBackupId 的记录
        Map<String, Object> emergency = new HashMap<>();
        emergency.put("id", 900L);
        emergency.put("backupName", "应急备份(恢复前)-xxx");
        emergency.put("backupType", "emergency");
        emergency.put("scope", "sourceBackupId=100");
        when(backupRecordMapper.selectBySourceBackupId(100L)).thenReturn(Collections.singletonList(emergency));

        RestoreServiceImpl service = buildFullService(backupRecordMapper, mock(DataSource.class));

        // 验证 findEmergencyBackupId 通过私有路径返回应急备份 ID
        Long id = invokeFindEmergencyBackupId(service, 100L);
        assertEquals(900L, id);
    }

    private Long invokeFindEmergencyBackupId(RestoreServiceImpl service, Long sourceBackupId) throws Exception {
        java.lang.reflect.Method method = RestoreServiceImpl.class.getDeclaredMethod("findEmergencyBackupId", Long.class);
        method.setAccessible(true);
        return (Long) method.invoke(service, sourceBackupId);
    }
}
