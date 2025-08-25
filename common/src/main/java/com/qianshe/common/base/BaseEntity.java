package com.qianshe.common.base;

import com.baomidou.mybatisplus.annotation.*;
import com.qianshe.common.annotation.ToStringForLong;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ToStringForLong
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 删除标记（0：正常；1：删除）
     */
    @TableLogic
    private Integer isDeleted = 0;
} 