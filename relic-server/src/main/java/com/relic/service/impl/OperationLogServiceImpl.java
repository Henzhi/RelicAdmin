package com.relic.service.impl;

import com.relic.mapper.OperationLogMapper;
import com.relic.service.OperationLogService;
import com.relic.vo.PageResultVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String[] CSV_HEADERS = {"ID", "操作人", "操作类型", "目标类型", "目标ID", "变更前", "变更后", "IP", "UserAgent", "操作时间"};
    private static final String[] EXCEL_HEADERS = {"ID", "操作人", "操作类型", "目标类型", "目标ID", "变更前", "变更后", "IP", "UserAgent", "操作时间"};

    @Override
    public PageResultVO<Map<String, Object>> listPage(Integer userId, String operationType, String targetType,
                                                       String keyword, String startTime, String endTime,
                                                       int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = operationLogMapper.selectByPage(
                userId, operationType, targetType, keyword, startTime, endTime, offset, pageSize);
        long total = operationLogMapper.countByPage(
                userId, operationType, targetType, keyword, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void exportCSV(Integer userId, String operationType, String targetType,
                          String keyword, String startTime, String endTime,
                          HttpServletResponse response) {
        List<Map<String, Object>> records = operationLogMapper.selectAllForExport(
                userId, operationType, targetType, keyword, startTime, endTime);
        String fileName = "operation_log_" + LocalDateTime.now().format(DF) + ".csv";
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode(fileName, "UTF-8").replace("+", "%20") + "\"");
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            writer.println(String.join(",", CSV_HEADERS));
            for (Map<String, Object> row : records) {
                writer.println(csvEscape(row.get("id")) + "," +
                        csvEscape(row.get("operatorName")) + "," +
                        csvEscape(row.get("operationType")) + "," +
                        csvEscape(row.get("targetType")) + "," +
                        csvEscape(row.get("targetId")) + "," +
                        csvEscape(row.get("oldValue")) + "," +
                        csvEscape(row.get("newValue")) + "," +
                        csvEscape(row.get("ip")) + "," +
                        csvEscape(row.get("userAgent")) + "," +
                        csvEscape(row.get("createdAt")));
            }
            writer.flush();
            log.info("导出操作日志CSV: {} 条", records.size());
        } catch (Exception e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出CSV失败: " + e.getMessage());
        }
    }

    @Override
    public void exportExcel(Integer userId, String operationType, String targetType,
                            String keyword, String startTime, String endTime,
                            HttpServletResponse response) {
        List<Map<String, Object>> records = operationLogMapper.selectAllForExport(
                userId, operationType, targetType, keyword, startTime, endTime);
        String fileName = "operation_log_" + LocalDateTime.now().format(DF) + ".xlsx";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("操作日志");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(EXCEL_HEADERS[i]);
            }
            int rowIdx = 1;
            for (Map<String, Object> record : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(record.get("id")));
                row.createCell(1).setCellValue(String.valueOf(record.get("operatorName")));
                row.createCell(2).setCellValue(String.valueOf(record.get("operationType")));
                row.createCell(3).setCellValue(String.valueOf(record.get("targetType")));
                row.createCell(4).setCellValue(String.valueOf(record.get("targetId")));
                row.createCell(5).setCellValue(nullToEmpty(record.get("oldValue")));
                row.createCell(6).setCellValue(nullToEmpty(record.get("newValue")));
                row.createCell(7).setCellValue(nullToEmpty(record.get("ip")));
                row.createCell(8).setCellValue(nullToEmpty(record.get("userAgent")));
                row.createCell(9).setCellValue(nullToEmpty(record.get("createdAt")));
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode(fileName, "UTF-8").replace("+", "%20") + "\"");
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            log.info("导出操作日志Excel: {} 条", records.size());
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void record(Long userId, String operationType, String targetType, String targetId,
                       String oldValue, String newValue, String ip, String userAgent) {
        operationLogMapper.insert(userId, operationType, targetType, targetId, oldValue, newValue, ip, userAgent);
    }

    private String csvEscape(Object val) {
        if (val == null) return "";
        String s = val.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private String nullToEmpty(Object val) {
        return val == null ? "" : val.toString();
    }
}