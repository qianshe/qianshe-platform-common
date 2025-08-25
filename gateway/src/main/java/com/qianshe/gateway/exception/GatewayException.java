package com.qianshe.gateway.exception;

import com.qianshe.gateway.model.Result;
import lombok.Getter;

/**
 * 网关自定义异常
 *
 * @author qianshe
 * @since 1.0.0
 */
@Getter
public class GatewayException extends RuntimeException {

    /**
     * 响应结果
     */
    private final Result<?> result;

    /**
     * 构造方法
     */
    public GatewayException(Result<?> result) {
        super(result.getMessage());
        this.result = result;
    }

    /**
     * 构造方法
     */
    public GatewayException(String message) {
        this(Result.fail(message));
    }

    /**
     * 构造方法
     */
    public GatewayException(int code, String message) {
        this(Result.fail(code, message));
    }
} 