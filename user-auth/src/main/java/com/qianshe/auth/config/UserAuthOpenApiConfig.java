package com.qianshe.auth.config;

import com.qianshe.common.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Auth服务OpenAPI配置
 */
@Configuration
public class UserAuthOpenApiConfig extends OpenApiConfig {
    /**
     * 覆盖基类配置，添加Auth特定信息
     */
    @Bean
    @Primary
    @Override
    public OpenAPI openAPI() {
        OpenAPI openAPI = super.openAPI();
        openAPI.getInfo()
                .title("认证服务API")
                .description("认证服务接口文档，提供用户认证、权限控制等功能");
        return openAPI;
    }
} 