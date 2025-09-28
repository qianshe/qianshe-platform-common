package com.qianshe.notification.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum NotificationType {
    
    /**
     * 系统通知
     */
    SYSTEM("SYSTEM", "系统通知"),
    
    /**
     * 匹配通知
     */
    MATCH("MATCH", "匹配通知"),
    
    /**
     * 审核通知
     */
    AUDIT("AUDIT", "审核通知"),
    
    /**
     * 消息通知
     */
    MESSAGE("MESSAGE", "消息通知"),
    
    /**
     * 营销通知
     */
    MARKETING("MARKETING", "营销通知"),
    
    /**
     * 安全通知
     */
    SECURITY("SECURITY", "安全通知");
    
    private final String code;
    private final String description;
    
    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
