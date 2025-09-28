package com.qianshe.notification.enums;

import lombok.Getter;

/**
 * 通知渠道枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum NotificationChannel {
    
    /**
     * 站内信
     */
    IN_APP("IN_APP", "站内信"),
    
    /**
     * 短信
     */
    SMS("SMS", "短信"),
    
    /**
     * 邮件
     */
    EMAIL("EMAIL", "邮件"),
    
    /**
     * 微信
     */
    WECHAT("WECHAT", "微信"),
    
    /**
     * 推送
     */
    PUSH("PUSH", "推送"),
    
    /**
     * 钉钉
     */
    DINGTALK("DINGTALK", "钉钉");
    
    private final String code;
    private final String description;
    
    NotificationChannel(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
