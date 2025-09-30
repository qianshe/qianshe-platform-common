package com.qianshe.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知模板实体
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notification_template")
public class NotificationTemplate {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码（唯一标识）
     */
    @TableField("template_code")
    private String templateCode;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 通知类型
     */
    @TableField("type")
    private NotificationType type;

    /**
     * 支持的通知渠道（逗号分隔）
     */
    @TableField("supported_channels")
    private String supportedChannels;

    /**
     * 标题模板
     */
    @TableField("title_template")
    private String titleTemplate;

    /**
     * 内容模板
     */
    @TableField("content_template")
    private String contentTemplate;

    /**
     * 模板参数说明（JSON格式）
     */
    @TableField("param_description")
    private String paramDescription;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled = true;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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

    /**
     * 检查是否支持指定渠道
     */
    public boolean supportsChannel(NotificationChannel channel) {
        return supportedChannels != null && supportedChannels.contains(channel.getCode());
    }
}
