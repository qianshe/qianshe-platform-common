package com.qianshe.operation.enums;

import lombok.Getter;

/**
 * 审核状态枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum AuditStatus {
    
    /**
     * 待审核
     */
    PENDING("PENDING", "待审核"),
    
    /**
     * 审核中
     */
    IN_REVIEW("IN_REVIEW", "审核中"),
    
    /**
     * 审核通过
     */
    APPROVED("APPROVED", "审核通过"),
    
    /**
     * 审核拒绝
     */
    REJECTED("REJECTED", "审核拒绝"),
    
    /**
     * 已撤回
     */
    WITHDRAWN("WITHDRAWN", "已撤回"),
    
    /**
     * 已过期
     */
    EXPIRED("EXPIRED", "已过期");
    
    private final String code;
    private final String description;
    
    AuditStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
