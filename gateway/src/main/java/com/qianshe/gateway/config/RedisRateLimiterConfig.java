package com.qianshe.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Redis 限流器配置类
 * 配置基于 Redis 的请求限流器
 *
 * @author qianshe
 * @since 1.0.0
 */
@Configuration
public class RedisRateLimiterConfig {

    /**
     * 默认限流配置
     * replenishRate: 令牌桶每秒填充速率
     * burstCapacity: 令牌桶总容量
     * requestedTokens: 每个请求消耗的令牌数
     *
     * @return Redis限流器
     */
    @Bean
    @Primary
    public RedisRateLimiter defaultRedisRateLimiter() {
        return new RedisRateLimiter(3, 5, 1);
    }

    /**
     * 认证服务限流配置
     * 登录注册等接口使用较宽松的限流策略
     *
     * @return Redis限流器
     */
    @Bean
    public RedisRateLimiter authRedisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * 星空创意资源对接平台限流配置
     * 资源匹配等业务接口使用中等限流策略
     *
     * @return Redis限流器
     */
    @Bean
    public RedisRateLimiter stellarRedisRateLimiter() {
        return new RedisRateLimiter(5, 10, 1);
    }
}