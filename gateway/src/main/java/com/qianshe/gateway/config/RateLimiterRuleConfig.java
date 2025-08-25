package com.qianshe.gateway.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流规则配置类
 * 从配置文件中读取自定义限流规则
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterRuleConfig {

    /**
     * 是否启用自定义限流
     */
    @Getter
    @Setter
    private boolean enabled = true;

    /**
     * 默认令牌桶容量
     */
    @Getter
    @Setter
    private int defaultCapacity = 10;

    /**
     * 默认令牌产生速率
     */
    @Getter
    @Setter
    private int defaultRate = 2;

    /**
     * 默认窗口期(秒)
     */
    @Getter
    @Setter
    private int defaultWindow = 10;

    /**
     * 限流规则列表
     */
    @Getter
    @Setter
    private List<RateLimiterRule> rules = new ArrayList<>();

    /**
     * 规则缓存，提高查询效率
     */
    private final Map<String, RateLimiterRule> ruleCache = new ConcurrentHashMap<>();

    /**
     * 初始化规则缓存
     */
    @PostConstruct
    public void init() {
        if (enabled && rules != null) {
            for (RateLimiterRule rule : rules) {
                ruleCache.put(rule.getPath(), rule);
                log.info("[限流配置] 加载限流规则: path={}, capacity={}, rate={}, window={}",
                        rule.getPath(), rule.getCapacity(), rule.getRate(), rule.getWindow());
            }
            log.info("[限流配置] 共加载 {} 条限流规则", rules.size());
        }
    }

    /**
     * 获取指定路径的限流规则
     *
     * @param path API路径
     * @return 限流规则，如果没有匹配的规则则返回null
     */
    public RateLimiterRule getRule(String path) {
        if (!enabled || path == null) {
            return null;
        }

        // 精确匹配
        RateLimiterRule rule = ruleCache.get(path);
        if (rule != null) {
            return rule;
        }

        // 前缀匹配
        for (Map.Entry<String, RateLimiterRule> entry : ruleCache.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 限流规则
     */
    @Data
    public static class RateLimiterRule {
        /**
         * API路径
         */
        private String path;

        /**
         * 令牌桶容量
         */
        private int capacity = 10;

        /**
         * 令牌产生速率
         */
        private int rate = 2;

        /**
         * 时间窗口(秒)
         */
        private int window = 60;
    }
} 