package com.qianshe.common.result;

import lombok.Getter;

/**
 * 返回状态码
 *
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(400, "操作失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(422, "参数校验失败"),
    ERROR(500, "系统异常"),
    
    // 用户相关: 1000-1999
    USER_NOT_FOUND(1000, "用户不存在"),
    USERNAME_ALREADY_EXISTS(1001, "用户名已存在"),
    MOBILE_ALREADY_EXISTS(1002, "手机号已存在"),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    VERIFY_CODE_ERROR(1006, "验证码错误或已过期"),
    PASSWORD_NOT_MATCH(1007, "两次密码不一致"),
    REGISTER_FAILED(1008, "注册失败"),
    INVALID_LOGIN_TYPE(1009, "无效的登录类型"),
    LOGIN_TYPE_NOT_SUPPORTED(1010, "不支持的登录类型"),
    OLD_PASSWORD_ERROR(1011, "旧密码错误"),
    UPDATE_PASSWORD_FAILED(1012, "修改密码失败"),
    USER_TYPE_ERROR(1013, "用户类型错误"),
    
    // 权限相关: 2000-2999
    TOKEN_EXPIRED(2000, "token已过期"),
    TOKEN_INVALID(2001, "token无效"),
    TOKEN_MISSING(2002, "token缺失"),
    ROLE_NOT_FOUND(2003, "角色不存在"),
    ASSIGN_ROLE_FAILED(2004, "分配角色失败"),
    REMOVE_ROLE_FAILED(2005, "移除角色失败"),
    UNAUTHORIZED_USER_TYPE(2006, "无权使用该用户类型登录"),
    PERMISSION_DENIED(2007, "权限不足"),
    
    // 业务相关: 3000-3999
    OPERATION_TOO_FREQUENT(3000, "操作过于频繁，请稍后重试"),
    DATA_NOT_FOUND(3001, "数据不存在"),
    DATA_ALREADY_EXISTS(3002, "数据已存在"),

    // 评论相关: 3100-3199
    COMMENT_NOT_FOUND(3100, "评论不存在"),
    COMMENT_NO_PERMISSION_DELETE(3101, "无权限删除该评论"),
    COMMENT_CONTENT_EMPTY(3102, "评论内容不能为空"),
    COMMENT_CONTENT_TOO_LONG(3103, "评论内容过长，最多支持1000个字符"),
    COMMENT_PARENT_NOT_FOUND(3104, "父评论不存在"),
    
    // 系统相关: 9000-9999
    SYSTEM_ERROR(9000, "系统异常"),
    NETWORK_ERROR(9001, "网络异常"),
    DATABASE_ERROR(9002, "数据库异常"),
    CACHE_ERROR(9003, "缓存异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 