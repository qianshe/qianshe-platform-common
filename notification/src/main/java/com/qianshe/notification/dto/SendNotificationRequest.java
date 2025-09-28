package com.qianshe.notification.dto;

import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 发送通知请求DTO
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "发送通知请求")
public class SendNotificationRequest {

    @Schema(description = "通知类型", required = true)
    @NotNull(message = "通知类型不能为空")
    private NotificationType type;

    @Schema(description = "通知渠道列表", required = true)
    @NotEmpty(message = "通知渠道不能为空")
    private List<NotificationChannel> channels;

    @Schema(description = "接收用户ID列表", required = true)
    @NotEmpty(message = "接收用户ID不能为空")
    private List<Long> receiverIds;

    @Schema(description = "发送用户ID")
    private Long senderId;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "扩展数据")
    private Map<String, Object> extraData;

    @Schema(description = "最大重试次数")
    private Integer maxRetryCount = 3;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (templateCode == null && (title == null || content == null)) {
            throw new IllegalArgumentException("模板编码和标题内容不能同时为空");
        }
    }
}
