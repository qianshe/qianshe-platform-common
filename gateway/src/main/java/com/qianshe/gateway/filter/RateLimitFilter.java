package com.qianshe.gateway.filter;

import com.qianshe.gateway.config.RateLimiterRuleConfig;
import com.qianshe.gateway.exception.GatewayException;
import com.qianshe.gateway.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义限流过滤器
 * 基于Redis实现令牌桶算法的限流机制
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RateLimiterRuleConfig rateLimiterRuleConfig;
    
    /**
     * Redis Lua脚本实现令牌桶算法
     */
    private static final String RATE_LIMITER_SCRIPT = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local timestamp = tonumber(ARGV[2]) " +
            "local rate = tonumber(ARGV[3]) " +
            "local window = tonumber(ARGV[4]) " +
            "local count = 0 " +
            "local bucket = redis.call('hgetall', key) " +
            "if table.getn(bucket) == 0 then " +
            "  redis.call('hset', key, 'tokens', capacity, 'timestamp', timestamp) " +
            "  redis.call('expire', key, window) " +
            "  return capacity - 1 " +
            "end " +
            "local available_tokens = tonumber(bucket[2]) " +
            "local last_timestamp = tonumber(bucket[4]) " +
            "local elapsed = timestamp - last_timestamp " +
            "local new_tokens = math.min(capacity, available_tokens + elapsed * rate) " +
            "if new_tokens < 1 then " +
            "  return -1 " +
            "else " +
            "  redis.call('hset', key, 'tokens', new_tokens - 1, 'timestamp', timestamp) " +
            "  redis.call('expire', key, window) " +
            "  return new_tokens - 1 " +
            "end";
    
    /**
     * 不需要限流的白名单路径
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/captcha",
            "/doc.html",
            "/swagger-resources",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars",
            "/error",
            "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果限流功能未启用，直接放行
        if (!rateLimiterRuleConfig.isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 白名单内的接口不限流
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        
        // 获取客户端IP
        String clientIp = request.getRemoteAddress().getAddress().getHostAddress();
        // 生成限流键（基于IP和请求路径）
        String key = "rate_limit:" + clientIp + ":" + path;
        
        // 获取限流规则
        RateLimiterRuleConfig.RateLimiterRule rule = rateLimiterRuleConfig.getRule(path);
        
        // 获取限流参数
        int capacity = rule != null ? rule.getCapacity() : rateLimiterRuleConfig.getDefaultCapacity();
        int rate = rule != null ? rule.getRate() : rateLimiterRuleConfig.getDefaultRate();
        int window = rule != null ? rule.getWindow() : rateLimiterRuleConfig.getDefaultWindow();
        
        // 创建Redis脚本
        RedisScript<Long> redisScript = RedisScript.of(RATE_LIMITER_SCRIPT, Long.class);
        
        // 构建参数列表
        List<String> keys = Arrays.asList(key);
        List<String> argsList = new ArrayList<>();
        argsList.add(String.valueOf(capacity));
        argsList.add(String.valueOf(Instant.now().getEpochSecond()));
        argsList.add(String.valueOf(rate));
        argsList.add(String.valueOf(window));
        
        // 执行Redis脚本并处理结果
        return redisTemplate.execute(redisScript, keys, argsList)
                .next() // 转换为Mono
                .flatMap(tokens -> {
                    if (tokens < 0) {
                        // 没有可用令牌，触发限流
                        log.warn("[网关]请求被限流: {}, IP: {}", path, clientIp);
                        return Mono.error(new GatewayException(Result.fail(429, "请求过于频繁，请稍后再试")));
                    } else {
                        // 限流日志记录
                        log.debug("[网关]限流检查通过: {}, IP: {}, 剩余令牌: {}", path, clientIp, tokens);
                        // 继续请求
                        return chain.filter(exchange);
                    }
                })
                .onErrorResume(GatewayException.class, e -> Mono.error(e))
                .onErrorResume(e -> {
                    log.error("[网关]限流检查异常: {}", e.getMessage());
                    return Mono.error(new GatewayException(Result.fail(500, "系统繁忙，请稍后再试")));
                });
    }

    @Override
    public int getOrder() {
        // 在AuthFilter之后执行
        return -90;
    }
} 