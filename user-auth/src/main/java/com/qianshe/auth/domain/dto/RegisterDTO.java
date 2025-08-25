package com.qianshe.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户注册DTO
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "用户注册请求参数")
public class RegisterDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "用户名必须为4-16位字母或数字")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(
        regexp = "^[A-Za-z0-9]{8,20}$",
        message = "密码必须为8-20位字母或数字"
    )
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 图形验证码标识
     * 从获取验证码接口的响应头中获取
     */
    @Schema(description = "图形验证码标识", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaKey;

    /**
     * 用户输入的图形验证码
     * 用户在前端输入的验证码字符串
     */
    @Schema(description = "用户输入的图形验证码", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ABCD")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
}