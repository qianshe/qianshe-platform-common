package com.qianshe.gateway.filter;

import com.qianshe.gateway.config.WhiteListConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 详细日志过滤器
 * 记录请求和响应的详细信息，便于排查问题
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter implements GlobalFilter, Ordered {

    private final WhiteListConfig whiteListConfig;

    /**
     * 最大日志大小
     */
    private static final int MAX_LOG_SIZE = 1024 * 10; // 10KB

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        String requestId = request.getId();
        
        // 记录基本请求信息
        log.info("[请求开始] {} {} - IP:{} ID:{}", method, path, clientIp, requestId);
        
        // 记录请求头
        logHeaders(request.getHeaders(), requestId);
        
        // 记录路由信息
        logRouteInfo(exchange, requestId);
        
        // 检查是否是白名单路径
        boolean isWhiteListed = whiteListConfig.getWhiteList().stream().anyMatch(path::startsWith);
        boolean isDetailLog = !isWhiteListed && log.isDebugEnabled();
        
        long startTime = System.currentTimeMillis();

        String requestEndLog = String.format("[请求结束] %s %s - 耗时:{}ms", method, path);
        // 如果需要记录详细日志且内容可能是JSON或表单
        MediaType contentType = request.getHeaders().getContentType();
        if (isDetailLog && contentType != null && 
                (contentType.includes(MediaType.APPLICATION_JSON) || 
                contentType.includes(MediaType.APPLICATION_FORM_URLENCODED))) {
            return logRequest(exchange, chain, startTime, requestId)
                    .doFinally(signalType -> log.info(requestEndLog, System.currentTimeMillis() - startTime));
        }
        
        // 普通请求只记录响应情况
        return logResponse(exchange, chain, startTime, requestId)
                .doFinally(signalType -> 
                        log.info(requestEndLog, System.currentTimeMillis() - startTime));
    }

    /**
     * 记录路由信息
     */
    private void logRouteInfo(ServerWebExchange exchange, String requestId) {
        // 获取路由信息
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route != null) {
            log.debug("[路由信息] {}: 路由ID={}, 目标URI={}", requestId, route.getId(), route.getUri());
        }
        
        // 获取转发目标地址
        Object gatewayRoute = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (gatewayRoute != null) {
            log.debug("[转发目标地址] {}: {}", requestId, gatewayRoute);
        }
        
        // 获取转发目标URI
        URI requestUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (requestUri != null) {
            log.info("[转发目标] {}: {}", requestId, requestUri);
            
            // 记录更多转发请求的详细信息
            log.info("[转发详情] {}: 方案={}, 主机={}, 端口={}, 路径={}, 查询参数={}", 
                    requestId, 
                    requestUri.getScheme(), 
                    requestUri.getHost(), 
                    requestUri.getPort(), 
                    requestUri.getPath(), 
                    requestUri.getQuery() != null ? requestUri.getQuery() : "无");
        }
        
        // 获取预测器路径变量
        Map<String, String> uriVariables = exchange.getAttribute(ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (uriVariables != null && !uriVariables.isEmpty()) {
            log.info("[路径变量] {}: {}", requestId, uriVariables);
        }
        
        // 获取过滤器应用情况
        Object gatewayFilters = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR + ".filters");
        if (gatewayFilters != null) {
            log.info("[应用过滤器] {}: {}", requestId, gatewayFilters);
        }
        
        // 获取原始请求信息
        ServerHttpRequest originalRequest = exchange.getRequest();
        log.debug("[原始请求] {}: 方法:{}, 路径:{}, 查询参数:{}",
                requestId, 
                originalRequest.getMethod(), 
                originalRequest.getURI().getPath(),
                originalRequest.getURI().getQuery() != null ? originalRequest.getURI().getQuery() : "无");
    }

    /**
     * 记录请求参数信息
     */
    private void logRequestParameters(ServerHttpRequest request, String requestId) {
        // 获取查询参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (!queryParams.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            queryParams.forEach((key, values) -> {
                if (!sb.isEmpty()) {
                    sb.append(", ");
                }
                sb.append(key).append("=[").append(String.join(",", values)).append("]");
            });
            log.info("[请求参数] {}: {}", requestId, sb);
        }
    }

    /**
     * 记录请求头信息
     */
    private void logHeaders(HttpHeaders headers, String requestId) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((name, values) -> {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(name).append("=[");
            
            // 敏感信息脱敏处理
            if ("Authorization".equalsIgnoreCase(name) || 
                "Cookie".equalsIgnoreCase(name) || 
                name.toLowerCase().contains("token") || 
                name.toLowerCase().contains("password")) {
                sb.append("***");
            } else {
                sb.append(String.join(",", values));
            }
            sb.append("]");
        });
        log.debug("[请求头] {}: {}", requestId, sb);
    }

    /**
     * 记录请求体和响应体
     */
    private Mono<Void> logRequest(ServerWebExchange exchange, GatewayFilterChain chain, long startTime, String requestId) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String path = uri.getPath();
        String method = request.getMethod().name();
        
        // 新增：记录请求参数
        logRequestParameters(request, requestId);
        
        // 获取请求体
        return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            String body = new String(bytes, StandardCharsets.UTF_8);

            // 记录请求信息
            log.info("[请求详情] {}: 方法={}, 路径={}, 请求体={}", requestId, method, path, body);

            // ✅ 使用装饰器重新封装请求，保留 body 内容
            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                }
            };
            // 继续处理请求
            mutatedRequest = mutatedRequest.mutate()
                .header("X-Request-ID", requestId)
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("[响应详情] {}: 状态码={}, 耗时={}ms", requestId, exchange.getResponse().getStatusCode(), duration);
                    }));
        });
    }

    /**
     * 记录响应信息
     */
    private Mono<Void> logResponse(ServerWebExchange exchange, GatewayFilterChain chain, long startTime, String requestId) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 包装响应
        ServerHttpResponse decoratedResponse = logResponse(exchange.getResponse(), startTime, path, method, requestId);
        
        // 继续处理
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
    
    /**
     * 包装响应，用于记录响应信息
     */
    private ServerHttpResponse logResponse(ServerHttpResponse response, long startTime, String path, String method, String requestId) {
        DataBufferFactory bufferFactory = response.bufferFactory();
        
        return new ServerHttpResponseDecorator(response) {
            private final AtomicBoolean loggedResponse = new AtomicBoolean(false);
            
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (log.isDebugEnabled() && body instanceof Flux<? extends DataBuffer> fluxBody) {

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        if (!loggedResponse.getAndSet(true)) {
                            // 记录响应状态
                            int status = this.getStatusCode() != null ? this.getStatusCode().value() : 500;
                            log.debug("[响应状态] {}: {}", requestId, status);
                            
                            // 记录响应头
                            HttpHeaders headers = this.getHeaders();
                            StringBuilder sb = new StringBuilder();
                            headers.forEach((name, values) -> {
                                sb.append(name).append("=[").append(String.join(",", values)).append("], ");
                            });
                            log.debug("[响应头] {}: {}", requestId, sb);
                            
                            // 处理响应体
                            if (this.getHeaders().getContentType() != null && 
                                    (this.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON) || 
                                    this.getHeaders().getContentType().includes(MediaType.TEXT_PLAIN))) {
                                StringBuilder responseBody = new StringBuilder();
                                dataBuffers.forEach(buffer -> {
                                    byte[] content = new byte[buffer.readableByteCount()];
                                    buffer.read(content);
                                    responseBody.append(new String(content, StandardCharsets.UTF_8));
                                    DataBufferUtils.release(buffer);
                                });
                                
                                // 记录响应体(可能需要截断)
                                String truncatedBody = truncateIfNeeded(responseBody.toString());
                                log.debug("[响应体] {}: {}", requestId, truncatedBody);
                                
                                // 重新创建数据缓冲区
                                return bufferFactory.wrap(responseBody.toString().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        
                        // 合并数据缓冲区
                        int size = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
                        DataBuffer buffer = bufferFactory.allocateBuffer(size);
                        dataBuffers.forEach(dataBuffer -> {
                            buffer.write(dataBuffer);
                            DataBufferUtils.release(dataBuffer);
                        });
                        
                        return buffer;
                    }));
                }
                
                return super.writeWith(body);
            }
        };
    }
    
    /**
     * 截断过大的日志内容
     */
    private String truncateIfNeeded(String content) {
        if (content != null && content.length() > MAX_LOG_SIZE) {
            return content.substring(0, MAX_LOG_SIZE) + "... [截断，内容过长]";
        }
        return content;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 