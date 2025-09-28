package com.qianshe.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础DTO类
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
public abstract class BaseDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建者ID
     */
    private Long createBy;
    
    /**
     * 更新者ID
     */
    private Long updateBy;
}
