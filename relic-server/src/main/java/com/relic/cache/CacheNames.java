package com.relic.cache;

/**
 * Spring Cache 缓存名常量。
 * 与 {@link com.relic.config.RedisConfiguration} 中按缓存名配置的 TTL 一一对应。
 */
public final class CacheNames {

    /** 仪表盘统计汇总，允许 1 分钟延迟 */
    public static final String DASHBOARD_OVERVIEW = "dashboard:overview";

    /** 角色→权限码映射，权限变更最迟 5 分钟生效 */
    public static final String ROLE_PERMISSIONS = "role-permissions";

    /** 低频变更字典表（朝代/博物馆/地点的 ID→名称映射） */
    public static final String DICT = "dict";

    private CacheNames() {
    }
}
