package com.qianshe.notification.enums;

import lombok.Getter;

/**
 * 通知状态枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum NotificationStatus {
    
    /**
     * 待发送
     */
    PENDING("PENDING", "待发送"),
    
    /**
     * 发送中
     */
    SENDING("SENDING", "发送中"),
    
    /**
     * 发送成功
     */
    SUCCESS("SUCCESS", "发送成功"),
    
    /**
     * 发送失败
     */
    FAILED("FAILED", "发送失败"),
    
    /**
     * 已读
     */
    READ("READ", "已读"),
    
    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");
    
    private final String code;
    private final String description;
    
    NotificationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
