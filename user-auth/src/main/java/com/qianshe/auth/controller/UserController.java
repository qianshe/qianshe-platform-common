package com.qianshe.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.qianshe.auth.service.RoleService;
import com.qianshe.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 展示如何在微服务中使用注解式鉴权
 *
 * @author qianshe
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final StpLogic userStpLogic;
    private final StpLogic adminStpLogic;
    private final StpLogic vipStpLogic;
    private final RoleService roleService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/user/info")
    @SaCheckLogin
    public Result<Map<String, Object>> getUserInfo() {
        // 获取当前登录的用户ID和类型
        Object loginId = getLoginId();
        String userType = getUserType();
        List<String> roles = roleService.getUserRoles(Long.valueOf(loginId.toString()));
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginId);
        userInfo.put("userType", userType);
        userInfo.put("roles", roles);
        
        return Result.ok(userInfo);
    }
    
    @Operation(summary = "管理员操作 - 角色检查")
    @GetMapping("/admin/role-check")
    @SaCheckRole("admin")
    public Result<String> adminRoleCheck() {
        return Result.ok("您具有管理员角色，可以访问此接口");
    }
    
    @Operation(summary = "VIP用户操作 - 角色检查")
    @GetMapping("/vip/role-check")
    @SaCheckRole("vip")
    public Result<String> vipRoleCheck() {
        return Result.ok("您具有VIP角色，可以访问此接口");
    }
    
    @Operation(summary = "多角色检查 - 任一角色")
    @GetMapping("/role-check/or")
    @SaCheckRole(value = {"admin", "vip"}, mode = SaMode.OR)
    public Result<String> roleCheckOr() {
        return Result.ok("您具有admin或vip角色之一，可以访问此接口");
    }
    
    @Operation(summary = "多角色检查 - 同时具有")
    @GetMapping("/role-check/and")
    @SaCheckRole(value = {"admin", "vip"}, mode = SaMode.AND)
    public Result<String> roleCheckAnd() {
        return Result.ok("您同时具有admin和vip角色，可以访问此接口");
    }
    
    @Operation(summary = "权限检查")
    @GetMapping("/permission-check")
    @SaCheckPermission("user:view")
    public Result<String> permissionCheck() {
        return Result.ok("您具有user:view权限，可以访问此接口");
    }
    
    @Operation(summary = "自定义鉴权")
    @GetMapping("/custom-auth")
    @SaCheckLogin
    public Result<String> customAuth() {
        // 自定义鉴权逻辑
        if (isAdmin()) {
            return Result.ok("您是管理员，可以访问此接口");
        } else if (isVip()) {
            return Result.ok("您是VIP用户，可以访问此接口");
        } else {
            return Result.ok("您是普通用户，可以访问此接口");
        }
    }
    
    /**
     * 获取当前登录的用户ID
     */
    private Object getLoginId() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId == null) {
            loginId = adminStpLogic.getLoginIdDefaultNull();
        }
        if (loginId == null) {
            loginId = vipStpLogic.getLoginIdDefaultNull();
        }
        return loginId;
    }
    
    /**
     * 获取当前登录的用户类型
     */
    private String getUserType() {
        if (adminStpLogic.isLogin()) {
            return "admin";
        } else if (vipStpLogic.isLogin()) {
            return "vip";
        } else {
            return "user";
        }
    }
    
    /**
     * 检查当前用户是否为管理员
     */
    private boolean isAdmin() {
        return adminStpLogic.isLogin() || StpUtil.hasRole("admin");
    }
    
    /**
     * 检查当前用户是否为VIP用户
     */
    private boolean isVip() {
        return vipStpLogic.isLogin() || StpUtil.hasRole("vip");
    }
} 