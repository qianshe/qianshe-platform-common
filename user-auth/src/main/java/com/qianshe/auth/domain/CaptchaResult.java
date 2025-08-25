package com.qianshe.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;

/**
 * 验证码结果实体类
 * 用于封装验证码生成的结果，包含验证码标识和图片对象
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码生成结果")
public class CaptchaResult {

    /**
     * 验证码唯一标识
     * 用于后续验证时关联对应的验证码文本
     */
    @Schema(description = "验证码唯一标识", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String captchaKey;

    /**
     * 验证码图片对象
     * BufferedImage类型，用于生成图片响应
     */
    @Schema(description = "验证码图片对象")
    private BufferedImage image;
}
