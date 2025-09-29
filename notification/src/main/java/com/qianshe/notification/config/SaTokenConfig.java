package com.qianshe.notification.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 * 通知服务只读取会话信息，不进行鉴权
 * 参考fantasy-core的实现，确保架构一致性
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token过滤器
     * 仅用于读取token信息，不进行鉴权
     * 鉴权由网关统一处理，微服务专注业务逻辑
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .setBeforeAuth(obj -> {
                    try {
                        // 只记录token存在性，不预先获取用户信息
                        String token = StpUtil.getTokenValue();
                        if (token != null) {
                            log.debug("[Sa-Token] 通知服务当前请求token: {}", token);
                        } else {
                            log.debug("[Sa-Token] 通知服务未提供token");
                        }
                    } catch (Exception e) {
                        log.warn("[Sa-Token] 通知服务处理token异常", e);
                    }
                });
    }
}
