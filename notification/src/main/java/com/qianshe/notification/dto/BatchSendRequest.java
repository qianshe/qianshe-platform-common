package com.qianshe.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量发送通知请求DTO
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "批量发送通知请求")
public class BatchSendRequest {

    @Schema(description = "通知请求列表", required = true)
    @NotEmpty(message = "通知请求列表不能为空")
    @Valid
    private List<SendNotificationRequest> notifications;

    @Schema(description = "是否异步发送")
    private Boolean async = true;

    @Schema(description = "批次标识")
    private String batchId;
}
