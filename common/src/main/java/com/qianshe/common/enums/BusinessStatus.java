package com.qianshe.common.enums;

/**
 * 业务状态枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
public enum BusinessStatus {
    
    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    
    /**
     * 已通过
     */
    APPROVED(1, "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),
    
    /**
     * 已删除
     */
    DELETED(3, "已删除");
    
    private final Integer code;
    private final String description;
    
    BusinessStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     * 
     * @param code 状态代码
     * @return 业务状态枚举
     */
    public static BusinessStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (BusinessStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的业务状态代码: " + code);
    }
}
