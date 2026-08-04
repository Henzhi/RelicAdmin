package com.relic.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * M-05：真实爬虫执行器单元测试
 * 使用 JVM 内嵌 HttpServer 提供本地测试数据源，验证 api/rss/web 三种解析。
 */
class CrawlExecutorTest {

    private static HttpServer server;
    private static String base;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        base = "http://127.0.0.1:" + server.getAddress().getPort();

        server.createContext("/api.json", exchange -> respond(exchange,
                "{\"data\":{\"list\":[{\"name\":\"铜鼎\",\"url\":\"/a/1\"},{\"name\":\"玉璧\",\"url\":\"/a/2\"}]}}"));
        server.createContext("/feed.xml", exchange -> respond(exchange,
                "<?xml version=\"1.0\"?><rss><channel><item><title>考古发现</title><link>http://x/1</link><description>描述A</description></item>"
                        + "<item><title>修复进展</title><link>http://x/2</link><description>描述B</description></item></channel></rss>"));
        server.createContext("/page.html", exchange -> respond(exchange,
                "<html><body><a href=\"/news/1\">第一条新闻</a><a href=\"/news/2\">第二条新闻</a><a href=\"#anchor\">锚点</a></body></html>"));

        server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    private static void respond(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private final CrawlExecutor executor = new CrawlExecutor();

    @Test
    void crawlApi_parsesJsonArrayWithItemsPath() {
        String rule = "{\"itemsPath\":\"data.list\",\"titleField\":\"name\",\"linkField\":\"url\"}";
        List<Map<String, Object>> items = executor.crawl(base + "/api.json", "api", rule, 10);
        assertEquals(2, items.size());
        assertEquals("铜鼎", items.get(0).get("title"));
        assertEquals("/a/1", items.get(0).get("link"));
        assertEquals("玉璧", items.get(1).get("title"));
    }

    @Test
    void crawlRss_parsesItems() {
        List<Map<String, Object>> items = executor.crawl(base + "/feed.xml", "rss", null, 10);
        assertEquals(2, items.size());
        assertEquals("考古发现", items.get(0).get("title"));
        assertEquals("http://x/1", items.get(0).get("link"));
        assertEquals("描述A", items.get(0).get("description"));
    }

    @Test
    void crawlWeb_extractsLinksAndSkipsAnchors() {
        List<Map<String, Object>> items = executor.crawl(base + "/page.html", "web", null, 10);
        assertEquals(2, items.size());
        assertEquals("第一条新闻", items.get(0).get("title"));
        assertEquals("/news/1", items.get(0).get("link"));
    }

    @Test
    void crawl_blankUrl_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> executor.crawl(" ", "web", null, 10));
    }

    @Test
    void crawl_apiMissingArray_throws() {
        assertThrows(IllegalStateException.class,
                () -> executor.crawl(base + "/page.html", "api", "{\"itemsPath\":\"not.exists\"}", 10));
    }

    @Test
    void crawl_httpError_throws() {
        assertThrows(IllegalStateException.class,
                () -> executor.crawl(base + "/api.json", "api", null, 1));
    }
}
