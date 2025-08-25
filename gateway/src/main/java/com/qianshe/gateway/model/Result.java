package com.qianshe.gateway.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一响应结果
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> ok(T data) {
        return new Result<T>()
                .setCode(200)
                .setMessage("操作成功")
                .setData(data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message);
    }

    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized(String message) {
        return fail(401, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> Result<T> forbidden(String message) {
        return fail(403, message);
    }

    /**
     * 转换为异常
     */
    public RuntimeException asException() {
        return new RuntimeException(this.message);
    }
} 