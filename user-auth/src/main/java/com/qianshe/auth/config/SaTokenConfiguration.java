package com.qianshe.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 * 认证服务需保留多账号体系支持，用于不同类型的用户登录
 *
 * @author qianshe
 * @since 1.0.0
 */
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {

    /**
     * 普通用户 StpLogic Bean
     */
    @Bean(name = "userStpLogic")
    @Primary
    public StpLogic userStpLogic() {
        return new StpLogic("user");
    }
    
    /**
     * 管理员 StpLogic Bean
     */
    @Bean(name = "adminStpLogic")
    public StpLogic adminStpLogic() {
        return new StpLogic("admin");
    }
    
    /**
     * VIP用户 StpLogic Bean
     */
    @Bean(name = "vipStpLogic")
    public StpLogic vipStpLogic() {
        return new StpLogic("vip");
    }
    
    /**
     * Sa-Token配置
     */
    @Bean
    @Primary
    public SaTokenConfig saTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        // Token名称
        config.setTokenName("Authorization");
        // Token有效期（30天）
        config.setTimeout(60 * 60 * 24 * 30);
        // Token临时有效期（30分钟）
        config.setActiveTimeout(1800);
        // 是否允许同一账号并发登录
        config.setIsConcurrent(true);
        // 在多人登录同一账号时，是否共用一个token
        config.setIsShare(true);
        // Token风格
        config.setTokenStyle("uuid");
        // 是否输出操作日志
        config.setIsLog(true);
        // 是否从cookie中读取token
        config.setIsReadCookie(false);
        // 是否在初始化配置时打印版本字符画
        config.setIsPrint(false);
        return config;
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有域名进行跨域调用
        config.addAllowedOriginPattern("*");
        // 允许跨越发送cookie
        config.setAllowCredentials(true);
        // 放行全部原始头信息
        config.addAllowedHeader("*");
        // 允许所有请求方法跨域调用
        config.addAllowedMethod("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }

    /**
     * 注册Sa-Token拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns(
                // 登录注册接口
                "/auth/login/**",
                "/auth/register",
                "/auth/isLogin",
                // 调试用接口（开发阶段）
                "/auth/getLoginId",
                "/auth/checkToken",
                // 接口文档相关
                "/doc.html",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**",
                // 网站图标
                "/favicon.ico",
                // 验证码接口
                "/captcha/**",
                // 健康检查
                "/actuator/**",
                // 错误页面
                "/error"
        );
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置knife4j静态资源映射
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 配置网站图标
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
    }
} 