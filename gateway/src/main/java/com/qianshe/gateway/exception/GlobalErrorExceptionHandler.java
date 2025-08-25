package com.qianshe.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qianshe.gateway.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);

        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        Result<?> result;

        if (ex instanceof GatewayException) {
            GatewayException ge = (GatewayException) ex;
            result = ge.getResult();
            log.warn("[网关]业务异常: {}, 请求路径: {}, 请求方法: {}", ge.getMessage(), path, method);
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            HttpStatusCode statusCode = rse.getStatusCode();
            int code = statusCode.value();
            String message;

            // 根据HTTP状态码返回不同的错误信息
            if (code == HttpStatus.NOT_FOUND.value()) {
                message = "请求的服务不存在";
                log.warn("[网关]服务未找到: 请求路径: {}, 请求方法: {}, 错误信息: {}", path, method, rse.getMessage());
            } else if (code == HttpStatus.UNAUTHORIZED.value()) {
                message = "未登录或登录已过期";
                log.warn("[网关]未授权访问: 请求路径: {}, 请求方法: {}", path, method);
            } else if (code == HttpStatus.FORBIDDEN.value()) {
                message = "没有权限访问该资源";
                log.warn("[网关]访问被拒绝: 请求路径: {}, 请求方法: {}", path, method);
            } else {
                message = "请求处理失败";
                log.warn("[网关]请求异常: 状态码: {}, 请求路径: {}, 请求方法: {}, 错误信息: {}",
                    code, path, method, rse.getMessage());
            }

            result = Result.fail(code, message);
        } else {
            log.error("[网关]系统异常: 请求路径: {}, 请求方法: {}, 异常信息: {}",
                path, method, ex.getMessage(), ex);
            result = Result.fail("系统异常，请稍后重试");
        }

        return response.writeWith(Mono.fromSupplier(() -> {
            try {
                return response.bufferFactory().wrap(objectMapper.writeValueAsBytes(result));
            } catch (JsonProcessingException e) {
                log.error("[网关]响应写入异常: 请求路径: {}, 请求方法: {}", path, method, e);
                return response.bufferFactory().wrap(new byte[0]);
            }
        }));
    }
}
