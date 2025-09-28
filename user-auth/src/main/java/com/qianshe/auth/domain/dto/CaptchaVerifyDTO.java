package com.qianshe.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码验证请求DTO
 * 用于接收前端传递的验证码验证参数
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "验证码验证请求参数")
public class CaptchaVerifyDTO {

    /**
     * 验证码标识
     * 从获取验证码接口的响应头中获取
     */
    @Schema(description = "验证码标识", requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaKey;

    /**
     * 用户输入的验证码
     * 用户在前端输入的验证码字符串
     */
    @Schema(description = "用户输入的验证码", requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "ABCD")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

}
