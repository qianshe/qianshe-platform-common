package com.qianshe.filestorage.enums;

import lombok.Getter;

/**
 * 文件访问权限类型枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum FileAccessType {
    
    /**
     * 公开访问 - 任何人都可以访问
     */
    PUBLIC("PUBLIC", "公开访问"),
    
    /**
     * 私有访问 - 仅文件所有者可以访问
     */
    PRIVATE("PRIVATE", "私有访问"),
    
    /**
     * 业务访问 - 基于业务权限控制访问
     */
    BUSINESS("BUSINESS", "业务访问"),
    
    /**
     * 管理员访问 - 仅管理员可以访问
     */
    ADMIN("ADMIN", "管理员访问");
    
    private final String code;
    private final String description;
    
    FileAccessType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
