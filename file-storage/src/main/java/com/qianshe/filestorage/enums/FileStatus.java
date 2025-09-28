package com.qianshe.filestorage.enums;

import lombok.Getter;

/**
 * 文件状态枚举
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum FileStatus {
    
    /**
     * 上传中
     */
    UPLOADING("UPLOADING", "上传中"),
    
    /**
     * 上传完成
     */
    UPLOADED("UPLOADED", "上传完成"),
    
    /**
     * 处理中
     */
    PROCESSING("PROCESSING", "处理中"),
    
    /**
     * 可用
     */
    AVAILABLE("AVAILABLE", "可用"),
    
    /**
     * 已删除
     */
    DELETED("DELETED", "已删除"),
    
    /**
     * 处理失败
     */
    FAILED("FAILED", "处理失败");
    
    private final String code;
    private final String description;
    
    FileStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
