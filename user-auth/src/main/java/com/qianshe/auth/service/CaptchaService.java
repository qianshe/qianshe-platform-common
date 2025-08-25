package com.qianshe.auth.service;

import com.qianshe.auth.domain.CaptchaResult;

/**
 * 验证码服务接口
 * 提供图形验证码和短信验证码的生成、验证功能
 *
 * @author qianshe
 * @since 1.0.0
 */
public interface CaptchaService {

    /**
     * 生成验证码并返回图片和key的结果对象
     * 原子化操作，一次性生成验证码文本、图片并存储，避免时序问题
     *
     * @return 验证码结果对象，包含标识和图片
     */
    CaptchaResult generateCaptchaWithImage();

    /**
     * 验证图形验证码
     * 验证用户输入的验证码是否正确，支持大小写不敏感验证
     * 验证成功后会自动删除Redis中的验证码，防止重复使用
     *
     * @param captchaKey  验证码标识
     * @param captchaCode 用户输入的验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    boolean validateCaptcha(String captchaKey, String captchaCode);

    /**
     * 生成短信验证码
     * 生成6位数字验证码并发送到指定手机号
     *
     * @param mobile 手机号码
     * @return 验证码标识（用于后续验证）
     */
    String generateSmsCode(String mobile);

    /**
     * 验证短信验证码
     * 验证用户输入的短信验证码是否正确
     * 验证成功后会自动删除Redis中的验证码，防止重复使用
     *
     * @param mobile   手机号码
     * @param smsCode  用户输入的短信验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    boolean validateSmsCode(String mobile, String smsCode);
} 