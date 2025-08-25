package com.qianshe.gateway.filter;

import com.qianshe.gateway.exception.GatewayException;
import com.qianshe.gateway.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * IP黑名单过滤器
 * 支持配置文件静态黑名单和Redis动态黑名单
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RefreshScope
public class IpBlackListFilter implements GlobalFilter, Ordered {

    /**
     * 黑名单IP列表，通过配置文件配置
     */
    @Value("${blacklist.ip:}")
    private String blacklistIpStr;

    /**
     * Redis客户端
     */
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    /**
     * Redis中黑名单的Key前缀
     */
    private static final String BLACKLIST_KEY = "gateway:blacklist:ip:";

    /**
     * 静态黑名单
     */
    private Set<String> staticBlacklist = new HashSet<>();

    public IpBlackListFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        initStaticBlacklist();
    }

    /**
     * 初始化静态黑名单
     */
    public void initStaticBlacklist() {
        if (blacklistIpStr != null && !blacklistIpStr.isEmpty()) {
            String[] ips = blacklistIpStr.split(",");
            staticBlacklist = new HashSet<>(Arrays.asList(ips));
            log.info("[IP黑名单] 静态黑名单初始化完成，共{}个IP", staticBlacklist.size());
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        
        // 检查静态黑名单
        if (staticBlacklist.contains(ip)) {
            log.warn("[IP黑名单] IP【{}】在静态黑名单中，拒绝访问", ip);
            return Mono.error(new GatewayException(Result.forbidden("您的IP已被列入黑名单，请联系管理员")));
        }

        // 检查动态黑名单
        return redisTemplate.hasKey(BLACKLIST_KEY + ip)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("[IP黑名单] IP【{}】在动态黑名单中，拒绝访问", ip);
                        return Mono.error(new GatewayException(Result.forbidden("您的IP已被列入黑名单，请联系管理员")));
                    } else {
                        return chain.filter(exchange);
                    }
                });
    }

    @Override
    public int getOrder() {
        // 在所有过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
} 