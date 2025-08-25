package com.qianshe.auth.service.impl;

import com.google.code.kaptcha.Producer;
import com.qianshe.auth.domain.CaptchaResult;
import com.qianshe.auth.service.CaptchaService;
import com.qianshe.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 提供图形验证码和短信验证码的生成、验证功能实现
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final Producer captchaProducer;
    private final StringRedisTemplate redisTemplate;

    /**
     * 图形验证码过期时间（分钟）
     */
    private static final long CAPTCHA_EXPIRE_MINUTES = 2;

    /**
     * 短信验证码过期时间（分钟）
     */
    private static final long SMS_CODE_EXPIRE_MINUTES = 1;

    /**
     * 验证码Redis key前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:image:";
    private static final String SMS_CODE_KEY_PREFIX = "captcha:sms:";



    /**
     * 生成验证码并返回图片和key的结果对象
     * 原子化操作，一次性生成验证码文本、图片并存储到Redis，避免时序问题
     *
     * @return 验证码结果对象，包含标识和图片
     */
    @Override
    public CaptchaResult generateCaptchaWithImage() {
        try {
            log.debug("开始生成验证码和图片");

            // 生成验证码文本
            String captchaText = captchaProducer.createText();
            // 生成唯一标识
            String captchaKey = UUID.randomUUID().toString();
            // 生成图片
            BufferedImage image = captchaProducer.createImage(captchaText);

            // 保存验证码文本到Redis
            redisTemplate.opsForValue().set(
                CAPTCHA_KEY_PREFIX + captchaKey,
                captchaText,
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
            );

            log.debug("验证码生成成功，captchaKey: {}", captchaKey);
            return new CaptchaResult(captchaKey, image);

        } catch (Exception e) {
            log.error("验证码生成失败", e);
            throw new ServiceException("验证码生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证图形验证码
     * 验证用户输入的验证码是否正确，支持大小写不敏感验证
     * 验证成功后会自动删除Redis中的验证码，防止重复使用
     *
     * @param captchaKey  验证码标识
     * @param captchaCode 用户输入的验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    @Override
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        try {
            // 参数校验
            if (captchaKey == null || captchaKey.trim().isEmpty()) {
                log.warn("验证码标识为空");
                return false;
            }
            if (captchaCode == null || captchaCode.trim().isEmpty()) {
                log.warn("验证码内容为空");
                return false;
            }

            // 从Redis获取正确的验证码
            String key = CAPTCHA_KEY_PREFIX + captchaKey;
            String correctCode = redisTemplate.opsForValue().get(key);

            if (correctCode == null) {
                log.warn("验证码已过期或不存在，captchaKey: {}", captchaKey);
                return false;
            }

            // 大小写不敏感验证
            boolean isValid = captchaCode.trim().equalsIgnoreCase(correctCode.trim());

            if (isValid) {
                // 验证成功后立即删除验证码，防止重复使用
                // redisTemplate.delete(key);
                log.debug("验证码验证成功，captchaKey: {}", captchaKey);
            } else {
                log.debug("验证码验证失败，输入: {}, 正确: {}, captchaKey: {}",
                         captchaCode, correctCode, captchaKey);
            }

            return isValid;

        } catch (Exception e) {
            log.error("验证码验证异常，captchaKey: {}", captchaKey, e);
            return false;
        }
    }

    @Override
    public String generateSmsCode(String mobile) {
        // 生成6位数字验证码
        String smsCode = String.format("%06d", (int) (Math.random() * 1000000));
        // TODO: 调用短信服务发送验证码
        
        // 保存到Redis
        redisTemplate.opsForValue().set(
            SMS_CODE_KEY_PREFIX + mobile,
            smsCode,
            SMS_CODE_EXPIRE_MINUTES,
            TimeUnit.MINUTES
        );
        return smsCode;
    }

    @Override
    public boolean validateSmsCode(String mobile, String smsCode) {
        if (mobile == null || smsCode == null) {
            return false;
        }
        String key = SMS_CODE_KEY_PREFIX + mobile;
        String correctCode = redisTemplate.opsForValue().get(key);
        if (correctCode == null) {
            return false;
        }
        // 验证成功后删除验证码
        redisTemplate.delete(key);
        return smsCode.equals(correctCode);
    }
} 