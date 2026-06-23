package com.relic.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.relic.context.BaseContext;
import com.relic.entity.Artifact;
import com.relic.entity.ImportHistory;
import com.relic.mapper.ArtifactMapper;
import com.relic.mapper.AdminUserMapper;
import com.relic.mapper.ImportHistoryMapper;
import com.relic.service.ArtifactImportService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtifactImportServiceImpl implements ArtifactImportService {

    private final ArtifactMapper artifactMapper;
    private final ImportHistoryMapper importHistoryMapper;
    private final AdminUserMapper adminUserMapper;

    /** 系统支持的字段列表 */
    private static final List<String> SYSTEM_FIELDS = List.of(
            "objectId", "titleZh", "titleEn", "timePeriod", "dynastyId", "type",
            "material", "description", "dimensions", "museumId", "locationId",
            "detailUrl", "imageUrl", "creditLine", "accessionNumber", "crawlDate"
    );

    /** 必填字段 */
    private static final Set<String> REQUIRED_FIELDS = Set.of("titleZh", "type");

    /** 字段中文映射 */
    private static final Map<String, String> FIELD_LABELS = Map.ofEntries(
            Map.entry("objectId", "文物编号"), Map.entry("titleZh", "中文名称"), Map.entry("titleEn", "英文名称"),
            Map.entry("timePeriod", "时期"), Map.entry("dynastyId", "朝代ID"), Map.entry("type", "类型"),
            Map.entry("material", "材质"), Map.entry("description", "描述"), Map.entry("dimensions", "尺寸"),
            Map.entry("museumId", "博物馆ID"), Map.entry("locationId", "地点ID"),
            Map.entry("detailUrl", "详情链接"), Map.entry("imageUrl", "图片链接"),
            Map.entry("creditLine", "来源"), Map.entry("accessionNumber", "入藏编号"), Map.entry("crawlDate", "采集日期")
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final Set<String> ALLOWED_TYPES = Set.of("csv", "xlsx", "xls");

    @Override
    public Map<String, Object> preview(MultipartFile file) {
        validateFile(file);
        List<Map<String, String>> rows = parseFile(file);
        String[] headers = rows.isEmpty() ? new String[0] : rows.get(0).keySet().toArray(new String[0]);

        // 自动匹配字段映射
        Map<String, String> autoMapping = autoMapFields(headers);

        // 只预览前20行
        List<Map<String, String>> previewRows = rows.size() > 20 ? rows.subList(0, 20) : rows;

        Map<String, Object> result = new HashMap<>();
        result.put("headers", headers);
        result.put("systemFields", SYSTEM_FIELDS);
        result.put("fieldLabels", FIELD_LABELS);
        result.put("autoMapping", autoMapping);
        result.put("previewRows", previewRows);
        result.put("totalRows", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> confirmImport(MultipartFile file, Map<String, String> fieldMapping) {
        validateFile(file);
        List<Map<String, String>> rows = parseFile(file);

        // 如果没有传映射，自动匹配
        if (fieldMapping == null || fieldMapping.isEmpty()) {
            String[] headers = rows.isEmpty() ? new String[0] : rows.get(0).keySet().toArray(new String[0]);
            fieldMapping = autoMapFields(headers);
        }

        // 创建导入历史记录
        ImportHistory history = new ImportHistory();
        history.setFileName(file.getOriginalFilename());
        history.setFileSize(file.getSize());
        history.setFileType(getFileExtension(file.getOriginalFilename()));
        history.setTotalCount(rows.size());
        history.setSuccessCount(0);
        history.setFailCount(0);
        history.setStatus("processing");
        history.setOperatorId(BaseContext.getCurrentId() != null ? BaseContext.getCurrentId().intValue() : null);
        if (history.getOperatorId() != null) {
            try {
                com.relic.entity.AdminUser operator = adminUserMapper.selectById(history.getOperatorId());
                if (operator != null) history.setOperatorName(operator.getRealName() != null ? operator.getRealName() : operator.getUsername());
            } catch (Exception ignored) {}
        }
        history.setStartedAt(LocalDateTime.now());
        importHistoryMapper.insert(history);

        // 逐行解析、验证、插入
        List<Map<String, Object>> errors = new ArrayList<>();
        int successCount = 0;
        int batchSize = 100;

        List<Artifact> batch = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            try {
                Artifact artifact = mapToArtifact(rows.get(i), fieldMapping);
                List<String> validationErrors = validateArtifact(artifact, i + 1);
                if (!validationErrors.isEmpty()) {
                    for (String err : validationErrors) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("row", i + 1);
                        error.put("type", "验证错误");
                        error.put("message", err);
                        errors.add(error);
                    }
                    continue;
                }
                batch.add(artifact);
                if (batch.size() >= batchSize) {
                    for (Artifact a : batch) {
                        try { artifactMapper.insert(a); successCount++; }
                        catch (Exception e) {
                            errors.add(createError(i + 1, "数据库错误", e.getMessage()));
                        }
                    }
                    batch.clear();
                }
            } catch (Exception e) {
                errors.add(createError(i + 1, "解析错误", e.getMessage()));
            }
        }
        // 处理剩余批次
        for (Artifact a : batch) {
            try { artifactMapper.insert(a); successCount++; }
            catch (Exception e) {
                errors.add(createError(rows.size(), "数据库错误", e.getMessage()));
            }
        }

        // 更新导入历史
        history.setSuccessCount(successCount);
        history.setFailCount(errors.size());
        history.setStatus("completed");
        history.setCompletedAt(LocalDateTime.now());
        // 错误报告只保留前100条
        if (!errors.isEmpty()) {
            List<Map<String, Object>> truncated = errors.size() > 100 ? errors.subList(0, 100) : errors;
            history.setErrorReport(com.alibaba.fastjson2.JSON.toJSONString(truncated));
        }
        importHistoryMapper.updateResult(history);

        Map<String, Object> result = new HashMap<>();
        result.put("importId", history.getId());
        result.put("totalCount", rows.size());
        result.put("successCount", successCount);
        result.put("failCount", errors.size());
        result.put("errors", errors.size() > 50 ? errors.subList(0, 50) : errors);
        result.put("hasMoreErrors", errors.size() > 50);
        return result;
    }

    @Override
    public PageResultVO<ImportHistory> listHistory(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ImportHistory> records = importHistoryMapper.selectByPage(offset, pageSize);
        long total = importHistoryMapper.count();
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public ImportHistory getHistoryDetail(Integer id) {
        return importHistoryMapper.selectById(id);
    }

    @Override
    public byte[] downloadTemplate(String fileType) {
        try {
            if ("csv".equalsIgnoreCase(fileType)) {
                return generateCsvTemplate();
            } else {
                return generateExcelTemplate();
            }
        } catch (Exception e) {
            throw new RuntimeException("生成模板失败: " + e.getMessage());
        }
    }

    // ========== 私有方法 ==========

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请上传文件");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("文件大小不能超过50MB");
        String ext = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_TYPES.contains(ext)) throw new IllegalArgumentException("仅支持CSV、XLSX、XLS格式文件");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private List<Map<String, String>> parseFile(MultipartFile file) {
        String ext = getFileExtension(file.getOriginalFilename());
        try {
            if ("csv".equals(ext)) return parseCsv(file.getInputStream());
            else return parseExcel(file.getInputStream(), ext);
        } catch (Exception e) {
            throw new RuntimeException("文件解析失败: " + e.getMessage());
        }
    }

    private List<Map<String, String>> parseCsv(InputStream is) throws IOException, CsvException {
        List<Map<String, String>> result = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(is, StandardCharsets.UTF_8))
                .withSkipLines(0).build()) {
            List<String[]> allRows = reader.readAll();
            if (allRows.isEmpty()) return result;
            String[] headers = allRows.get(0);
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0; j < headers.length && j < row.length; j++) {
                    map.put(headers[j].trim(), row[j] != null ? row[j].trim() : "");
                }
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, String>> parseExcel(InputStream is, String ext) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        Workbook workbook = ext.equals("xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet.getPhysicalNumberOfRows() == 0) { workbook.close(); return result; }

        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) headers.add(getCellValue(cell));

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Map<String, String> map = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                map.put(headers.get(j), cell != null ? getCellValue(cell) : "");
            }
            // 跳过全空行
            if (map.values().stream().allMatch(v -> v == null || v.isBlank())) continue;
            result.add(map);
        }
        workbook.close();
        return result;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double val = cell.getNumericCellValue();
                yield val == Math.floor(val) ? String.valueOf((long) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /** 自动匹配文件列名到系统字段 */
    private Map<String, String> autoMapFields(String[] headers) {
        Map<String, String> mapping = new LinkedHashMap<>();
        for (String header : headers) {
            String h = header.trim();
            // 精确匹配系统字段名
            if (SYSTEM_FIELDS.contains(h)) { mapping.put(h, h); continue; }
            // 匹配中文标签
            for (Map.Entry<String, String> entry : FIELD_LABELS.entrySet()) {
                if (entry.getValue().equals(h)) { mapping.put(h, entry.getKey()); break; }
            }
            if (mapping.containsKey(h)) continue;
            // 模糊匹配
            String lower = h.toLowerCase();
            if (lower.contains("名称") || lower.contains("title") && lower.contains("zh")) mapping.put(h, "titleZh");
            else if (lower.contains("name") && lower.contains("en")) mapping.put(h, "titleEn");
            else if (lower.contains("编号") || lower.contains("object")) mapping.put(h, "objectId");
            else if (lower.contains("类型") || lower.contains("type")) mapping.put(h, "type");
            else if (lower.contains("材质") || lower.contains("material")) mapping.put(h, "material");
            else if (lower.contains("朝代") || lower.contains("dynasty")) mapping.put(h, "dynastyId");
            else if (lower.contains("博物馆") || lower.contains("museum")) mapping.put(h, "museumId");
            else if (lower.contains("地点") || lower.contains("location")) mapping.put(h, "locationId");
            else if (lower.contains("描述") || lower.contains("desc")) mapping.put(h, "description");
            else if (lower.contains("尺寸") || lower.contains("dim")) mapping.put(h, "dimensions");
            else if (lower.contains("时期") || lower.contains("period")) mapping.put(h, "timePeriod");
            else if (lower.contains("来源") || lower.contains("credit")) mapping.put(h, "creditLine");
            else if (lower.contains("入藏") || lower.contains("accession")) mapping.put(h, "accessionNumber");
            else if (lower.contains("采集") || lower.contains("crawl")) mapping.put(h, "crawlDate");
            else if (lower.contains("图片") || lower.contains("image")) mapping.put(h, "imageUrl");
            else if (lower.contains("链接") || lower.contains("url") || lower.contains("detail")) mapping.put(h, "detailUrl");
        }
        return mapping;
    }

    /** 将行数据映射为Artifact实体 */
    private Artifact mapToArtifact(Map<String, String> row, Map<String, String> fieldMapping) {
        Artifact artifact = new Artifact();
        for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
            String fileColumn = entry.getKey();
            String systemField = entry.getValue();
            String value = row.get(fileColumn);
            if (value == null || value.isBlank()) continue;

            switch (systemField) {
                case "objectId" -> artifact.setObjectId(value);
                case "titleZh" -> artifact.setTitleZh(value);
                case "titleEn" -> artifact.setTitleEn(value);
                case "timePeriod" -> artifact.setTimePeriod(value);
                case "dynastyId" -> artifact.setDynastyId(parseInteger(value));
                case "type" -> artifact.setType(value);
                case "material" -> artifact.setMaterial(value);
                case "description" -> artifact.setDescription(value);
                case "dimensions" -> artifact.setDimensions(value);
                case "museumId" -> artifact.setMuseumId(parseInteger(value));
                case "locationId" -> artifact.setLocationId(parseInteger(value));
                case "detailUrl" -> artifact.setDetailUrl(value);
                case "imageUrl" -> artifact.setImageUrl(value);
                case "creditLine" -> artifact.setCreditLine(value);
                case "accessionNumber" -> artifact.setAccessionNumber(value);
                case "crawlDate" -> artifact.setCrawlDate(parseDate(value));
            }
        }
        return artifact;
    }

    /** 数据验证 */
    private List<String> validateArtifact(Artifact artifact, int rowNum) {
        List<String> errors = new ArrayList<>();
        if (artifact.getTitleZh() == null || artifact.getTitleZh().isBlank())
            errors.add("中文名称不能为空");
        if (artifact.getType() == null || artifact.getType().isBlank())
            errors.add("类型不能为空");
        if (artifact.getTitleZh() != null && artifact.getTitleZh().length() > 255)
            errors.add("中文名称长度不能超过255");
        if (artifact.getObjectId() != null && artifact.getObjectId().length() > 100)
            errors.add("文物编号长度不能超过100");
        if (artifact.getDescription() != null && artifact.getDescription().length() > 5000)
            errors.add("描述长度不能超过5000");
        return errors;
    }

    private Integer parseInteger(String value) {
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "yyyy年MM月dd日"};
        for (String pattern : patterns) {
            try { return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern(pattern)); }
            catch (Exception ignored) {}
        }
        try { return LocalDate.parse(value.trim()); } catch (Exception e) { return null; }
    }

    private Map<String, Object> createError(int row, String type, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("row", row);
        error.put("type", type);
        error.put("message", message);
        return error;
    }

    private byte[] generateCsvTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("文物编号,中文名称,英文名称,时期,朝代ID,类型,材质,描述,尺寸,博物馆ID,地点ID,详情链接,图片链接,来源,入藏编号,采集日期,流转脉络,当前状态\n");
        sb.append("RELIC001,青花瓷瓶,Blue and White Vase,明代,5,瓷器,青花瓷,明代青花瓷瓶,高45cm,1,1,https://example.com,https://img.com/1.jpg,故宫博物院捐赠,GZ2024001,2024-01-01,故宫博物院收藏,展出中\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generateExcelTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("文物数据导入模板");

        // 表头行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"文物编号", "中文名称", "英文名称", "时期", "朝代ID", "类型", "材质", "描述", "尺寸", "博物馆ID", "地点ID", "详情链接", "图片链接", "来源", "入藏编号", "采集日期", "流转脉络", "当前状态"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 示例行
        Row sampleRow = sheet.createRow(1);
        String[] sampleData = {"RELIC001", "青花瓷瓶", "Blue and White Vase", "明代", "5", "瓷器", "青花瓷", "明代青花瓷瓶", "高45cm", "1", "1", "https://example.com", "https://img.com/1.jpg", "故宫博物院捐赠", "GZ2024001", "2024-01-01", "故宫博物院收藏", "展出中"};
        for (int i = 0; i < sampleData.length; i++) {
            sampleRow.createCell(i).setCellValue(sampleData[i]);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
