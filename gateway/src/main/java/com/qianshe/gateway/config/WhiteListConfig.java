package com.qianshe.gateway.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "gateway.security")
@Slf4j
public class WhiteListConfig {

    /**
     * 白名单路径列表
     * 这些路径不会经过鉴权过程，任何人都可以访问
     * 注意：关键的认证接口如/auth/isLogin不应该在此列表中，应该通过正常的鉴权流程验证token
     */
    private List<String> whiteList;

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
        log.info("[WhiteListConfig] 白名单配置已更新: {}", whiteList);
    }
    
    /**
     * 检查路径是否在白名单中
     *
     * @param path 请求路径
     * @return 是否在白名单中
     */
    public boolean isInWhiteList(String path) {
        for (String whitePath : whiteList) {
            if (path.startsWith(whitePath)) {
                log.debug("[WhiteListConfig] 路径在白名单中: {}, 匹配规则: {}", path, whitePath);
                return true;
            }
        }
        log.debug("[WhiteListConfig] 路径不在白名单中: {}", path);
        return false;
    }
} 