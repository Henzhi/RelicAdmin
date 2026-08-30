package com.relic.service;

import java.util.Map;

/**
 * 低频变更字典表的缓存读取服务。
 *
 * <p>将 朝代/博物馆/地点 的「ID → 名称」映射整表缓存（30 分钟 TTL），
 * 供文物列表等高频查询替代逐行 selectById 的 N+1 访问。
 * 字典增删改时由对应 Service 通过 @CacheEvict(cacheNames="dict") 失效整个缓存组。</p>
 */
public interface DictCacheService {

    /** 朝代 ID → 中文名称 */
    Map<Integer, String> getDynastyNames();

    /** 博物馆 ID → 名称 */
    Map<Integer, String> getMuseumNames();

    /** 地点 ID → 中文名称 */
    Map<Integer, String> getLocationNames();
}
