package com.qianshe.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param message 返回消息
     * @param data    返回数据
     */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误消息
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     */
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Result<T> validateFailed() {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMessage(), null);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), null);
    }

    // ========== 兼容性方法 ==========
    /**
     * 成功返回结果（兼容性方法，等同于ok(String message, T data)）
     *
     * @param message 返回消息
     * @param data    返回数据
     */
    public static <T> Result<T> success(String message, T data) {
        return ok(message, data);
    }

    /**
     * 失败返回结果（兼容性方法，等同于fail()）
     */
    public static <T> Result<T> error() {
        return fail();
    }

    /**
     * 失败返回结果（兼容性方法，等同于fail(String message)）
     *
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return fail(message);
    }

    /**
     * 失败返回结果（兼容性方法，等同于fail(Integer code, String message)）
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return fail(code, message);
    }

    /**
     * 失败返回结果（兼容性方法，等同于fail(Integer code, String message, T data)）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return fail(code, message, data);
    }

    // ========== 实例方法 ==========

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 判断是否失败
     *
     * @return 是否失败
     */
    public boolean isFail() {
        return !isSuccess();
    }

    /**
     * 成功响应（泛型方法）
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return Result对象
     */
    public static Result<Void> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（布尔值）
     *
     * @param success 成功标志
     * @return Result对象
     */
    public static Result<Boolean> success(Boolean success) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), success);
    }

    /**
     * 成功响应（整数）
     *
     * @param value 整数值
     * @return Result对象
     */
    public static Result<Integer> success(Integer value) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), value);
    }
}