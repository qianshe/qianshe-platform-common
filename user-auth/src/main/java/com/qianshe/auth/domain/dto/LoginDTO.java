package com.qianshe.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录DTO
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求参数")
public class LoginDTO {

    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "凭证（密码/验证码）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "凭证不能为空")
    private String credential;

    @Schema(description = "登录类型（1：用户名密码，2：手机号验证码，3：邮箱密码）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "登录类型不能为空")
    private String loginType;
    
    @Schema(description = "用户类型（user：普通用户，admin：管理员，vip：VIP用户）", example = "user")
    private String userType = "user";

    /**
     * 图形验证码
     */
    private String captcha;

    /**
     * 图形验证码key
     */
    private String captchaKey;
} 