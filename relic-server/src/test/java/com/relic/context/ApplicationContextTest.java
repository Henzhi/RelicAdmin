package com.relic.context;

import com.relic.interceptor.JwtTokenAdminInterceptor;
import com.relic.interceptor.JwtTokenKnowledgeInterceptor;
import com.relic.interceptor.JwtTokenMuseumInterceptor;
import com.relic.interceptor.JwtTokenUserInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Spring 上下文加载测试：验证移除 spring.main.allow-circular-references 后
 * 容器仍能正常初始化。
 * <p>DataSource / RedisConnectionFactory 使用 Mock，避免连接真实外部依赖；
 * MyBatis 自动配置保留，确保 Mapper/Service/Aspect 全部业务 bean 完整装配，
 * 若存在循环依赖或缺失 bean 将在此测试中直接失败。</p>
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
                "spring.task.scheduling.enabled=false",
                "relic.redis.host=localhost",
                "relic.redis.port=6379",
                "relic.redis.password=",
                "relic.redis.database=0",
                "relic.jwt.admin-secret-key=QjnEXgrR7DaaP2quggUJrQED2hVCKVHUcKaHRdMGHdU=",
                "relic.jwt.knowledge-secret-key=QjnEXgrR7DaaP2quggUJrQED2hVCKVHUcKaHRdMGHdU=",
                "relic.jwt.museum-secret-key=QjnEXgrR7DaaP2quggUJrQED2hVCKVHUcKaHRdMGHdU=",
                "relic.jwt.user-secret-key=QjnEXgrR7DaaP2quggUJrQED2hVCKVHUcKaHRdMGHdU="
        })
@ActiveProfiles("test")
class ApplicationContextTest {

    /** Mock 数据源，避免测试连接真实数据库 */
    @MockBean
    private DataSource dataSource;

    /** Mock Redis 连接工厂，避免测试连接远程 Redis */
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    /** Mock StringRedisTemplate（LoginAttemptService 依赖），Redis 自动配置已被排除 */
    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenKnowledgeInterceptor jwtTokenKnowledgeInterceptor;

    @Autowired
    private JwtTokenMuseumInterceptor jwtTokenMuseumInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Test
    void contextLoads() {
        assertNotNull(jwtTokenAdminInterceptor);
        assertNotNull(jwtTokenKnowledgeInterceptor);
        assertNotNull(jwtTokenMuseumInterceptor);
        assertNotNull(jwtTokenUserInterceptor);
    }
}
