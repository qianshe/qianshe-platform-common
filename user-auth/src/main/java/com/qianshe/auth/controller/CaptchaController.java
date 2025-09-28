package com.qianshe.auth.controller;

import com.qianshe.auth.domain.CaptchaResult;
import com.qianshe.auth.domain.dto.CaptchaVerifyDTO;
import com.qianshe.auth.service.CaptchaService;
import com.qianshe.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * 验证码控制器
 *
 * @author qianshe
 * @since 1.0.0
 */
@Tag(name = "验证码管理")
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取图形验证码
     *
     * @param response HTTP响应对象
     */
    @Operation(summary = "获取图形验证码")
    @GetMapping("/image")
    public void getCaptcha(HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            log.debug("开始生成验证码图片");

            // 使用新的方法一次性生成验证码和图片，避免时序问题
            CaptchaResult captchaResult = captchaService.generateCaptchaWithImage();

            // 设置响应头
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setDateHeader("Expires", 0);
            response.setHeader("Captcha-Key", captchaResult.getCaptchaKey());

            // 输出图片
            outputStream = response.getOutputStream();
            ImageIO.write(captchaResult.getImage(), "jpeg", outputStream);
            outputStream.flush();

            log.debug("验证码图片生成成功，captchaKey: {}", captchaResult.getCaptchaKey());

        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            try {
                // 设置错误响应
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                if (outputStream == null) {
                    outputStream = response.getOutputStream();
                }
                String errorJson = "{\"code\":500,\"message\":\"验证码生成失败\"}";
                outputStream.write(errorJson.getBytes("UTF-8"));
                outputStream.flush();
            } catch (IOException ioException) {
                log.error("写入错误响应失败", ioException);
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 验证图形验证码
     * 验证用户输入的验证码是否正确，支持大小写不敏感验证
     *
     * @param verifyDTO 验证码验证请求参数
     * @return 验证结果
     */
    @Operation(summary = "验证图形验证码")
    @PostMapping("/verify")
    public Result<Boolean> verifyCaptcha(@Valid @RequestBody CaptchaVerifyDTO verifyDTO) {
        try {
            log.debug("开始验证验证码，captchaKey: {}", verifyDTO.getCaptchaKey());

            // 验证验证码
            boolean isValid = captchaService.validateCaptcha(
                verifyDTO.getCaptchaKey(),
                verifyDTO.getCaptchaCode()
            );

            if (isValid) {
                log.debug("验证码验证成功，captchaKey: {}", verifyDTO.getCaptchaKey());
                return Result.ok("验证码验证成功", true);
            } else {
                log.debug("验证码验证失败，captchaKey: {}", verifyDTO.getCaptchaKey());
                return Result.fail("验证码错误或已过期");
            }

        } catch (Exception e) {
            log.error("验证码验证异常，captchaKey: {}", verifyDTO.getCaptchaKey(), e);
            return Result.fail("验证码验证失败");
        }
    }

    /**
     * 发送短信验证码
     * 生成并发送短信验证码到指定手机号
     *
     * @param mobile 手机号码
     * @return 发送结果
     */
    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/{mobile}")
    public Result<Void> sendSmsCode(
        @Parameter(description = "手机号") @PathVariable String mobile
    ) {
        try {
            log.debug("开始发送短信验证码，mobile: {}", mobile);
            captchaService.generateSmsCode(mobile);
            log.debug("短信验证码发送成功，mobile: {}", mobile);
            return Result.ok("短信验证码发送成功", null);
        } catch (Exception e) {
            log.error("短信验证码发送失败，mobile: {}", mobile, e);
            return Result.fail("短信验证码发送失败");
        }
    }
}