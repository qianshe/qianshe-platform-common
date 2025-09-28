package com.qianshe.operation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qianshe.operation.enums.AuditStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审核任务实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("audit_task")
public class AuditTask {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务ID
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 审核标题
     */
    @TableField("title")
    private String title;

    /**
     * 审核内容
     */
    @TableField("content")
    private String content;

    /**
     * 审核状态
     */
    @TableField("status")
    private AuditStatus status = AuditStatus.PENDING;

    /**
     * 提交用户ID
     */
    @TableField("submitter_id")
    private Long submitterId;

    /**
     * 审核员ID
     */
    @TableField("auditor_id")
    private Long auditorId;

    /**
     * 审核意见
     */
    @TableField("audit_comment")
    private String auditComment;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority = 1;

    /**
     * 截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 扩展数据
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
