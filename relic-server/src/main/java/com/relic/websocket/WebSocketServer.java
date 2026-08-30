package com.relic.websocket;

import com.relic.constant.JwtClaimsConstant;
import com.relic.properties.JwtProperties;
import com.relic.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 *
 * <p>C-03 安全加固：建立连接时必须携带有效 JWT（query 参数 token），
 * 未通过鉴权的连接将直接关闭；sid 需与 Token 内声明的用户标识一致。</p>
 */
@Component
@Slf4j
@ServerEndpoint(value = "/ws/{sid}", configurator = WebSocketServer.SpringConfigurator.class)
public class WebSocketServer implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    //存放会话对象（线程安全）
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    private static JwtProperties jwtProperties;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        WebSocketServer.applicationContext = context;
    }

    /**
     * 连接建立成功调用的方法（C-03：先鉴权，再建立会话）
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        try {
            // 从容器中获取配置（@ServerEndpoint 实例非 Spring 管理，需手动注入）
            if (jwtProperties == null && applicationContext != null) {
                jwtProperties = applicationContext.getBean(JwtProperties.class);
            }
            // 从握手请求 query 中获取 token
            Map<String, java.util.List<String>> params = session.getRequestParameterMap();
            java.util.List<String> tokens = params.get("token");
            String token = (tokens != null && !tokens.isEmpty()) ? tokens.get(0) : null;

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket 连接被拒绝：缺少 token, sid={}", sid);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "missing token"));
                return;
            }
            if (jwtProperties == null || jwtProperties.getAdminSecretKey() == null) {
                log.error("WebSocket 鉴权失败：JWT 配置未初始化");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "server not ready"));
                return;
            }
            // 校验 JWT（管理端密钥）
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Object userIdClaim = claims.get(JwtClaimsConstant.USER_ID);
            if (userIdClaim == null) {
                log.warn("WebSocket 连接被拒绝：token 缺少 {} 声明, sid={}", JwtClaimsConstant.USER_ID, sid);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "invalid token"));
                return;
            }
            String tokenUserId = String.valueOf(userIdClaim);
            // sid 必须与 Token 内用户标识一致，防止越权订阅他人会话
            if (sid == null || !sid.equals(tokenUserId)) {
                log.warn("WebSocket 连接被拒绝：sid 与 token 不匹配, sid={}", sid);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "sid mismatch"));
                return;
            }
            sessionMap.put(sid, session);
            log.info("客户端：{} 建立连接（鉴权通过）", sid);
        } catch (Exception e) {
            log.warn("WebSocket 连接被拒绝：token 校验失败, sid={}, error={}", sid, e.getMessage());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "unauthorized"));
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端：{} 的信息:{}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid, Session session) {
        log.info("连接断开:{}", sid);
        // 仅当 map 中保存的是当前会话时才移除，避免同一管理员开多个连接时误删新会话
        sessionMap.remove(sid, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 会话异常：{}", error.getMessage());
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                // 异步发送：避免某个慢客户端阻塞通知线程
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error("WebSocket 发送消息失败", e);
            }
        }
    }

    /**
     * Spring 集成：使 @ServerEndpoint 实例可访问 Spring 容器中的 Bean
     */
    public static class SpringConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            if (applicationContext != null) {
                return applicationContext.getBean(endpointClass);
            }
            return super.getEndpointInstance(endpointClass);
        }
    }
}
