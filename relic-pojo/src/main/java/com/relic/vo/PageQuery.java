package com.relic.vo;

import lombok.Getter;

import java.util.List;

/**
 * 分页参数工具：统一归一化分页参数、计算 offset 并构造分页结果。
 *
 * <p>解决的问题：
 * <ol>
 *   <li>消除全项目 38 处重复的 {@code (page - 1) * pageSize} 计算与参数校验；</li>
 *   <li>pageSize 上限保护（{@link #MAX_PAGE_SIZE}）：防止恶意大分页一次拉全表，减轻数据库压力；</li>
 *   <li>page 下限保护：page &lt; 1 时归一化为 1，避免 offset 为负导致 SQL 异常。</li>
 * </ol>
 *
 * <p>用法：
 * <pre>{@code
 * PageQuery pq = PageQuery.of(page, pageSize);
 * List<X> records = mapper.selectByPage(..., pq.getOffset(), pq.getPageSize());
 * long total = mapper.countByPage(...);
 * return pq.toResult(total, records);
 * }</pre>
 */
@Getter
public class PageQuery {

    /** 单页最大条数，防止恶意大分页请求拖垮数据库 */
    public static final int MAX_PAGE_SIZE = 100;

    private final int page;
    private final int pageSize;
    private final int offset;

    private PageQuery(int page, int pageSize) {
        this.page = Math.max(page, 1);
        this.pageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        this.offset = (this.page - 1) * this.pageSize;
    }

    public static PageQuery of(int page, int pageSize) {
        return new PageQuery(page, pageSize);
    }

    /**
     * 构造分页结果，records 已被截断为当前页数据
     */
    public <T> PageResultVO<T> toResult(long total, List<T> records) {
        return new PageResultVO<>(total, records, page, pageSize);
    }
}
