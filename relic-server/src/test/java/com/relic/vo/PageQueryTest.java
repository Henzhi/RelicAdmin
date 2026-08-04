package com.relic.vo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PageQuery 单元测试：验证分页参数归一化（page≥1、pageSize 上限保护）。
 */
class PageQueryTest {

    @Test
    void normalPage_shouldComputeOffset() {
        PageQuery pq = PageQuery.of(2, 10);
        assertEquals(2, pq.getPage());
        assertEquals(10, pq.getPageSize());
        assertEquals(10, pq.getOffset());
    }

    @Test
    void pageBelowOne_shouldNormalizeToOne() {
        PageQuery pq = PageQuery.of(0, 10);
        assertEquals(1, pq.getPage());
        assertEquals(0, pq.getOffset());

        PageQuery negative = PageQuery.of(-5, 10);
        assertEquals(1, negative.getPage());
        assertEquals(0, negative.getOffset());
    }

    @Test
    void pageSizeZero_shouldNormalizeToOne() {
        PageQuery pq = PageQuery.of(1, 0);
        assertEquals(1, pq.getPageSize());
        assertEquals(0, pq.getOffset());
    }

    @Test
    void oversizedPageSize_shouldBeCapped() {
        // 防恶意大分页：pageSize=100000 应被截断为 MAX_PAGE_SIZE（防 BUG-003 全表拉取回归）
        PageQuery pq = PageQuery.of(1, 100000);
        assertEquals(PageQuery.MAX_PAGE_SIZE, pq.getPageSize());
        assertTrue(pq.getPageSize() <= PageQuery.MAX_PAGE_SIZE);
    }

    @Test
    void toResult_shouldWrapRecords() {
        PageQuery pq = PageQuery.of(3, 20);
        List<String> records = List.of("a", "b");
        PageResultVO<String> result = pq.toResult(55L, records);
        assertEquals(55L, result.getTotal());
        assertEquals(records, result.getRecords());
        assertEquals(3, result.getPage());
        assertEquals(20, result.getPageSize());
    }
}
