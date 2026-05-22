package com.relic.service.impl;

import com.relic.mapper.SystemLogMapper;
import com.relic.service.SystemLogService;
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
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogMapper systemLogMapper;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String[] EXCEL_HEADERS = {"ID", "日志级别", "模块", "消息", "详细信息", "记录时间"};

    @Override
    public PageResultVO<Map<String, Object>> listPage(String logLevel, String module, String keyword,
                                                       String startTime, String endTime,
                                                       int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = systemLogMapper.selectByPage(
                logLevel, module, keyword, startTime, endTime, offset, pageSize);
        long total = systemLogMapper.countByPage(logLevel, module, keyword, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void exportCSV(String logLevel, String module, String keyword,
                          String startTime, String endTime, HttpServletResponse response) {
        List<Map<String, Object>> records = systemLogMapper.selectAllForExport(
                logLevel, module, keyword, startTime, endTime);
        String fileName = "system_log_" + LocalDateTime.now().format(DF) + ".csv";
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode(fileName, "UTF-8").replace("+", "%20") + "\"");
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            writer.println("ID,日志级别,模块,消息,详细信息,记录时间");
            for (Map<String, Object> row : records) {
                writer.println(csvEscape(row.get("id")) + "," + csvEscape(row.get("logLevel")) + "," +
                        csvEscape(row.get("module")) + "," + csvEscape(row.get("message")) + "," +
                        csvEscape(row.get("detail")) + "," + csvEscape(row.get("createdAt")));
            }
            writer.flush();
            log.info("导出系统日志CSV: {} 条", records.size());
        } catch (Exception e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出CSV失败: " + e.getMessage());
        }
    }

    @Override
    public void exportExcel(String logLevel, String module, String keyword,
                            String startTime, String endTime, HttpServletResponse response) {
        List<Map<String, Object>> records = systemLogMapper.selectAllForExport(
                logLevel, module, keyword, startTime, endTime);
        String fileName = "system_log_" + LocalDateTime.now().format(DF) + ".xlsx";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("系统日志");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(EXCEL_HEADERS[i]);
            }
            int rowIdx = 1;
            for (Map<String, Object> record : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(record.get("id")));
                row.createCell(1).setCellValue(nullToEmpty(record.get("logLevel")));
                row.createCell(2).setCellValue(nullToEmpty(record.get("module")));
                row.createCell(3).setCellValue(nullToEmpty(record.get("message")));
                row.createCell(4).setCellValue(nullToEmpty(record.get("detail")));
                row.createCell(5).setCellValue(nullToEmpty(record.get("createdAt")));
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode(fileName, "UTF-8").replace("+", "%20") + "\"");
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            log.info("导出系统日志Excel: {} 条", records.size());
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void record(String logLevel, String module, String message, String detail) {
        systemLogMapper.insert(logLevel, module, message, detail);
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