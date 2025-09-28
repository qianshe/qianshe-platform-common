package com.qianshe.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求DTO
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
public class PageRequestDTO {
    
    /**
     * 页码（从0开始）
     */
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;
    
    /**
     * 排序字段
     */
    private String sortBy = "createTime";
    
    /**
     * 排序方向（asc/desc）
     */
    private String sortDirection = "desc";
}
