package com.qianshe.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.qianshe.gateway.exception.GatewayException;
import com.qianshe.gateway.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Sa-Token网关统一鉴权配置
 * 实现多账号体系的认证和鉴权
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SaTokenConfig {
    private final WhiteListConfig whiteListConfig;
    
    // 各类型用户的StpLogic实例
    private static final StpLogic adminStpLogic = new StpLogic("admin");
    private static final StpLogic vipStpLogic = new StpLogic("vip");
    private static final StpLogic userStpLogic = new StpLogic("user");
    
    // 初始化：设置默认StpLogic
    static {
        StpUtil.setStpLogic(userStpLogic);
    }
    
    /**
     * 注册Sa-Token全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")  // 拦截所有请求
                .addExclude(whiteListConfig.getWhiteList().toArray(new String[0]))  // 白名单放行
                .setAuth(obj -> {
                    try {
                        // 获取请求路径
                        String path = SaHolder.getRequest().getRequestPath();
                        log.debug("[网关]处理请求: {}", path);
                        
                        // 白名单请求直接放行
                        if (whiteListConfig.isInWhiteList(path)) {
                            log.debug("[网关]白名单请求放行: {}", path);
                            return;
                        }
                        
                        // 获取token
                        String token = SaHolder.getRequest().getHeader(StpUtil.getTokenName());
                        if (token == null || token.isEmpty()) {
                            log.warn("[网关]未提供token");
                            throw new GatewayException(Result.unauthorized("请先登录"));
                        }
                        
                        // 尝试获取用户信息和角色
                        StpLogic currentStpLogic = null;
                        Object loginId = null;
                        
                        // 先尝试admin
                        try {
                            loginId = adminStpLogic.getLoginIdByToken(token);
                            if (loginId != null) {
                                currentStpLogic = adminStpLogic;
                                log.debug("[网关]检测到admin用户: {}", loginId);
                            }
                        } catch (Exception e) {
                            log.debug("[网关]非admin用户token");
                        }
                        
                        // 再尝试vip
                        if (loginId == null) {
                            try {
                                loginId = vipStpLogic.getLoginIdByToken(token);
                                if (loginId != null) {
                                    currentStpLogic = vipStpLogic;
                                    log.debug("[网关]检测到vip用户: {}", loginId);
                                }
                            } catch (Exception e) {
                                log.debug("[网关]非vip用户token");
                            }
                        }
                        
                        // 最后尝试普通用户
                        if (loginId == null) {
                            try {
                                loginId = userStpLogic.getLoginIdByToken(token);
                                if (loginId != null) {
                                    currentStpLogic = userStpLogic;
                                    log.debug("[网关]检测到普通用户: {}", loginId);
                                }
                            } catch (Exception e) {
                                log.debug("[网关]非普通用户token");
                            }
                        }
                        
                        // 如果都无法获取用户信息，说明token无效
                        if (loginId == null || currentStpLogic == null) {
                            log.warn("[网关]无效的token: {}", token);
                            throw new GatewayException(Result.unauthorized("登录已失效，请重新登录"));
                        }
                        
                        // 检查token状态
                        checkTokenStatus(currentStpLogic, token);
                        
                        // 设置当前StpLogic
                        StpUtil.setStpLogic(currentStpLogic);
                        
                        // 转发用户信息
                        forwardUserInfo(loginId, currentStpLogic);
                        
                    } catch (GatewayException e) {
                        throw e;
                    } catch (Exception e) {
                        log.error("[网关]认证异常", e);
                        throw new GatewayException(Result.unauthorized("认证失败：" + e.getMessage()));
                    }
                })
                .setError(e -> {
                    // 统一异常处理
                    if (e instanceof GatewayException) {
                        return Mono.just(((GatewayException) e).getResult());
                    }
                    return Mono.just(Result.fail(HttpStatus.UNAUTHORIZED.value(), "认证失败：" + e.getMessage()));
                });
    }
    
    /**
     * 检查token状态
     * 包括：是否被冻结、是否过期等
     */
    private void checkTokenStatus(StpLogic stpLogic, String token) {
        // 检查token是否被冻结
        Object frozenObj = stpLogic.getTokenSessionByToken(token).get("frozen");
        boolean isFrozen = Optional.ofNullable(frozenObj)
                .map(obj -> Boolean.parseBoolean(obj.toString()))
                .orElse(false);
                
        if (isFrozen) {
            log.warn("[网关]token已被冻结: {}", token);
            throw new GatewayException(Result.unauthorized("账号已被冻结，请联系管理员"));
        }
        
        // 检查token是否过期
        long timeout = stpLogic.getTokenTimeout(token);
        if (timeout <= 0) {
            log.warn("[网关]token已过期: {}", token);
            throw new GatewayException(Result.unauthorized("登录已过期，请重新登录"));
        }
    }
    
    /**
     * 转发用户信息到下游服务
     */
    private void forwardUserInfo(Object loginId, StpLogic stpLogic) {
        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        if (exchange != null) {
            try {
                // 获取用户类型和角色信息
                String userType = stpLogic.getLoginType();
                List<String> roles = stpLogic.getSession().get("roles", Collections::emptyList);
                String rolesStr = String.join(",", roles);
                
                // 构建新的请求，添加用户信息到请求头
                ServerHttpRequest newRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(loginId))
                        .header("X-User-Type", userType)
                        .header("X-User-Roles", rolesStr)
                        .build();
                
                // 更新exchange中的请求
                exchange.mutate().request(newRequest).build();
                
                // 在token会话中存储用户信息
                stpLogic.getTokenSession()
                    .set("loginId", loginId)
                    .set("userType", userType)
                    .set("roles", roles);
                
                // 设置当前StpLogic
                StpUtil.setStpLogic(stpLogic);
                
                log.debug("[网关]转发用户信息: userId={}, userType={}, roles={}", loginId, userType, rolesStr);
            } catch (Exception e) {
                log.error("[网关]转发用户信息异常", e);
            }
        }
    }
}