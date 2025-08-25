package com.qianshe.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * OpenAPI统一配置
 */
@EnableKnife4j
@Configuration
public class OpenApiConfig {


    /**
     * 配置OpenAPI基本信息
     */
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "sa-token";
        // 创建API基本信息
        Info info = new Info()
                .title("空想家用户认证中心API")
                .version("1.0.0")
                .description("空想家用户认证中心接口文档")
                .contact(new Contact()
                        .name("技术支持")
                        .email("cutoffother@gmail.com"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT"));

        // 返回OpenAPI配置
        return new OpenAPI()
                .info(info)
                // 添加全局安全要求
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                // 定义安全方案
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("Authorization") // 与 Sa-Token 的 token 名称保持一致
                                .type(SecurityScheme.Type.APIKEY) // 使用 API Key 类型
                                .in(SecurityScheme.In.HEADER))) // 放在请求头中
                ;
    }
    // 添加 Knife4j 分组配置，显式指定安全方案
    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> {
                    // 为所有接口添加安全要求
                    openApi.getPaths().values().forEach(pathItem ->
                            pathItem.readOperations().forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList("sa-token"))
                            )
                    );
                })
                .build();
    }
} 