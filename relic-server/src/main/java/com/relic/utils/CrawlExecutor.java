package com.relic.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * M-05：真实爬虫执行器（替换原随机数模拟实现）
 *
 * <p>支持三种数据源类型：</p>
 * <ul>
 *   <li>{@code api}：JSON 接口，crawlRule 可配置 itemsPath（JSON 数组路径）、titleField、linkField</li>
 *   <li>{@code rss}：RSS/XML，自动提取 channel/item 下的 title、link、description</li>
 *   <li>{@code web}：HTML 页面，按 {@code linkPattern}（默认 {@code <a href="...">...</a>}）提取链接与文本</li>
 * </ul>
 *
 * <p>crawlRule JSON 示例：</p>
 * <pre>{@code
 * {
 *   "itemsPath": "data.list",
 *   "titleField": "name",
 *   "linkField": "url",
 *   "maxItems": 100
 * }
 * }</pre>
 *
 * <p>安全说明：仅发起 HTTP(S) GET 请求读取公开数据源，不执行页面脚本，不写入本地文件。</p>
 */
@Component
@Slf4j
public class CrawlExecutor {

    /** 默认单次抓取条数上限，防止超大数据源拖垮任务 */
    private static final int DEFAULT_MAX_ITEMS = 200;

    private static final Pattern HTML_LINK_PATTERN =
            Pattern.compile("<a[^>]+href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>([^<]{1,200})</a>", Pattern.CASE_INSENSITIVE);

    /**
     * 执行一次真实抓取
     *
     * @param sourceUrl       数据源 URL
     * @param sourceType      数据源类型：web / api / rss
     * @param crawlRuleJson   JSON 规则（可为 null/空，使用默认规则）
     * @param timeoutSeconds  超时时间（秒）
     * @return 抓取到的条目列表（每条为字段 Map：title / link / description）
     */
    public List<Map<String, Object>> crawl(String sourceUrl, String sourceType, String crawlRuleJson, Integer timeoutSeconds) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            throw new IllegalArgumentException("数据源URL不能为空");
        }
        String body = doGet(sourceUrl, timeoutSeconds);
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("抓取失败：数据源无响应或返回空内容");
        }

        String type = sourceType == null ? "web" : sourceType.trim().toLowerCase();
        JSONObject rule = parseRule(crawlRuleJson);
        int maxItems = rule.getIntValue("maxItems", DEFAULT_MAX_ITEMS);

        switch (type) {
            case "api":
                return parseApi(body, rule, maxItems);
            case "rss":
                return parseRss(body, maxItems);
            case "web":
            default:
                return parseWeb(body, rule, maxItems);
        }
    }

    /** 发送 GET 请求，带超时控制 */
    private String doGet(String url, Integer timeoutSeconds) {
        int timeoutMs = (timeoutSeconds != null && timeoutSeconds > 0)
                ? timeoutSeconds * 1000
                : 5 * 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeoutMs)
                .setSocketTimeout(timeoutMs)
                .setConnectionRequestTimeout(timeoutMs)
                .build();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setConfig(config);
            request.setHeader("User-Agent", "Mozilla/5.0 (compatible; RelicAdminCrawler/1.0)");
            request.setHeader("Accept", "text/html,application/json,application/xml,*/*");
            try (CloseableHttpResponse response = client.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != 200) {
                    throw new IllegalStateException("抓取失败：HTTP " + status);
                }
                byte[] bytes = response.getEntity() == null
                        ? new byte[0]
                        : EntityUtils.toByteArray(response.getEntity());
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("抓取失败：" + e.getMessage(), e);
        }
    }

    private JSONObject parseRule(String crawlRuleJson) {
        if (crawlRuleJson == null || crawlRuleJson.isBlank()) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(crawlRuleJson);
        } catch (Exception e) {
            log.warn("crawlRule 解析失败，使用默认规则: {}", e.getMessage());
            return new JSONObject();
        }
    }

    /** API：JSON 数组提取 */
    private List<Map<String, Object>> parseApi(String body, JSONObject rule, int maxItems) {
        List<Map<String, Object>> items = new ArrayList<>();
        String itemsPath = rule.getString("itemsPath");
        String titleField = rule.getString("titleField");
        String linkField = rule.getString("linkField");

        JSONArray array = null;
        try {
            if (itemsPath != null && !itemsPath.isBlank()) {
                array = resolveArray(JSON.parseObject(body), itemsPath);
            } else if (JSON.parse(body) instanceof JSONArray) {
                array = (JSONArray) JSON.parse(body);
            }
        } catch (Exception e) {
            throw new IllegalStateException("API 返回内容不是合法 JSON：" + e.getMessage(), e);
        }
        if (array == null) {
            throw new IllegalStateException("API 返回中未找到数组，请检查 crawlRule.itemsPath");
        }

        for (int i = 0; i < array.size() && items.size() < maxItems; i++) {
            Object obj = array.get(i);
            if (!(obj instanceof JSONObject)) {
                Map<String, Object> raw = new HashMap<>();
                raw.put("title", String.valueOf(obj));
                items.add(raw);
                continue;
            }
            JSONObject json = (JSONObject) obj;
            Map<String, Object> item = new HashMap<>();
            item.put("title", titleField != null ? json.getString(titleField) : json.getString("title"));
            item.put("link", linkField != null ? json.getString(linkField) : json.getString("url"));
            item.put("description", json.getString("description"));
            items.add(item);
        }
        return items;
    }

    /** RSS：XML item 提取（简易解析，无需额外依赖） */
    private List<Map<String, Object>> parseRss(String body, int maxItems) {
        List<Map<String, Object>> items = new ArrayList<>();
        String bodyLower = body;
        int idx = 0;
        while (idx < bodyLower.length() && items.size() < maxItems) {
            int start = bodyLower.indexOf("<item", idx);
            if (start < 0) break;
            int end = bodyLower.indexOf("</item>", start);
            if (end < 0) break;
            String block = bodyLower.substring(start, end);
            items.add(extractRssItem(block));
            idx = end + 7;
        }
        return items;
    }

    private Map<String, Object> extractRssItem(String block) {
        Map<String, Object> item = new HashMap<>();
        item.put("title", extractXmlTag(block, "title"));
        item.put("link", extractXmlTag(block, "link"));
        item.put("description", extractXmlTag(block, "description"));
        return item;
    }

    private String extractXmlTag(String block, String tag) {
        Pattern p = Pattern.compile("<" + tag + "[^>]*>(.*?)</" + tag + ">", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(block);
        return m.find() ? m.group(1).trim() : null;
    }

    /** Web：HTML 链接提取 */
    private List<Map<String, Object>> parseWeb(String body, JSONObject rule, int maxItems) {
        List<Map<String, Object>> items = new ArrayList<>();
        Matcher matcher = HTML_LINK_PATTERN.matcher(body);
        while (matcher.find() && items.size() < maxItems) {
            String href = matcher.group(1).trim();
            String text = matcher.group(2).trim();
            if (href.startsWith("#") || href.startsWith("javascript:")) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("title", text);
            item.put("link", href);
            item.put("description", null);
            items.add(item);
        }
        return items;
    }

    /** 按点分隔路径解析 JSON 数组，如 data.list.items */
    private JSONArray resolveArray(JSONObject root, String path) {
        String[] parts = path.split("\\.");
        Object current = root;
        for (String part : parts) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(part);
            } else {
                return null;
            }
        }
        return current instanceof JSONArray ? (JSONArray) current : null;
    }
}
