package com.qianshe.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流配置类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Configuration
public class RateLimiterConfig {

    /**
     * IP地址限流键解析器
     * 根据请求的IP地址进行限流
     *
     * @return IP地址限流键解析器
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }

    /**
     * 用户限流键解析器
     * 根据用户ID进行限流，从请求头中获取用户ID
     *
     * @return 用户限流键解析器
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("user-id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }

    /**
     * 接口路径限流键解析器
     * 根据请求的URI路径进行限流
     *
     * @return 接口路径限流键解析器
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
} 