package com.qianshe.common.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 安全工具类
 * 获取当前登录用户信息
 * 鉴权由网关统一处理，此类只用于读取信息
 */
@Slf4j
@Component
public class SecurityUtils {
    
    // 各类型用户的StpLogic实例
    private static final StpLogic adminStpLogic = new StpLogic("admin");
    private static final StpLogic vipStpLogic = new StpLogic("vip");
    private static final StpLogic userStpLogic = new StpLogic("user");

    /**
     * 获取当前登录用户ID
     * 优化：优先从网关传递的请求头获取，避免Redis查询
     *
     * @return 用户ID，未登录返回null
     */
    public static Long getUserId() {
        // 方案1：优先从请求头获取（网关传递，0次Redis查询）
        try {
            String userIdHeader = cn.dev33.satoken.context.SaHolder.getRequest().getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                Long userId = Long.parseLong(userIdHeader);
                log.debug("[SecurityUtils] 从请求头获取用户ID: {}", userId);
                return userId;
            }
        } catch (Exception e) {
            log.debug("[SecurityUtils] 从请求头获取用户ID失败: {}", e.getMessage());
        }

        // 方案2：从token session获取（网关已设置，1次Redis查询）
        try {
            Object loginId = StpUtil.getTokenSession().get("loginId");
            if (loginId != null) {
                Long userId = Long.parseLong(loginId.toString());
                log.debug("[SecurityUtils] 从token session获取用户ID: {}", userId);
                return userId;
            }
        } catch (Exception e) {
            log.debug("[SecurityUtils] 从token session获取用户ID失败: {}", e.getMessage());
        }

        // 方案3：降级到原来的复杂查询（兼容性，多次Redis查询）
        String token = StpUtil.getTokenValue();
        if (token == null) {
            log.debug("[SecurityUtils] 未提供token");
            return null;
        }

        log.debug("[SecurityUtils] 使用降级方案查询用户ID，token: {}", token);
        Object loginId = tryGetLoginId(token);
        if (loginId != null) {
            Long userId = Long.parseLong(loginId.toString());
            log.debug("[SecurityUtils] 降级方案获取到用户ID: {}", userId);
            return userId;
        }
        throw new NotLoginException("未能获取到用户ID", NotLoginException.INVALID_TOKEN, NotLoginException.INVALID_TOKEN);
    }

    /**
     * 获取当前用户类型
     * 优化：优先从网关传递的请求头获取，避免Redis查询
     */
    public static String getUserType() {
        try {
            // 方案1：优先从请求头获取（网关传递，0次Redis查询）
            try {
                String userTypeHeader = cn.dev33.satoken.context.SaHolder.getRequest().getHeader("X-User-Type");
                if (userTypeHeader != null && !userTypeHeader.isEmpty()) {
                    log.debug("[SecurityUtils] 从请求头获取用户类型: {}", userTypeHeader);
                    return userTypeHeader;
                }
            } catch (Exception e) {
                log.debug("[SecurityUtils] 从请求头获取用户类型失败: {}", e.getMessage());
            }

            // 方案2：从token session获取（网关已设置，1次Redis查询）
            try {
                Object userType = StpUtil.getTokenSession().get("userType");
                if (userType != null) {
                    String userTypeStr = userType.toString();
                    log.debug("[SecurityUtils] 从token session获取用户类型: {}", userTypeStr);
                    return userTypeStr;
                }
            } catch (Exception e) {
                log.debug("[SecurityUtils] 从token session获取用户类型失败: {}", e.getMessage());
            }

            // 方案3：降级到原来的复杂查询（兼容性）
            StpLogic currentLogic = getCurrentStpLogic();
            String userType = currentLogic != null ? currentLogic.getLoginType() : null;
            log.debug("[SecurityUtils] 降级方案获取用户类型: {}", userType);
            return userType;
        } catch (Exception e) {
            log.warn("[SecurityUtils] 获取用户类型异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户角色列表
     */
    public static List<String> getUserRoles() {
        try {
            StpLogic currentLogic = getCurrentStpLogic();
            if (currentLogic != null) {
                return currentLogic.getSession().get("roles", Collections::emptyList);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("[SecurityUtils] 获取用户角色异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 判断当前是否登录
     */
    public static boolean isLogin() {
        try {
            return getUserId() != null;
        } catch (Exception e) {
            log.warn("[SecurityUtils] 检查登录状态异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 尝试从各个StpLogic中获取用户ID
     */
    private static Object tryGetLoginId(String token) {
        // 尝试从admin账号获取
        try {
            Object loginId = adminStpLogic.getLoginIdByToken(token);
            if (loginId != null) {
                StpUtil.setStpLogic(adminStpLogic);
                return loginId;
            }
        } catch (Exception ignored) {}

        // 尝试从vip账号获取
        try {
            Object loginId = vipStpLogic.getLoginIdByToken(token);
            if (loginId != null) {
                StpUtil.setStpLogic(vipStpLogic);
                return loginId;
            }
        } catch (Exception ignored) {}

        // 尝试从普通用户账号获取
        try {
            Object loginId = userStpLogic.getLoginIdByToken(token);
            if (loginId != null) {
                StpUtil.setStpLogic(userStpLogic);
                return loginId;
            }
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * 获取当前用户对应的StpLogic
     */
    private static StpLogic getCurrentStpLogic() {
        String token = StpUtil.getTokenValue();
        if (token == null) {
            return null;
        }

        // 尝试从各个StpLogic中获取用户信息
        if (tryGetLoginId(token) != null) {
            return StpUtil.getStpLogic();
        }

        return null;
    }

    /**
     * 判断当前是否是管理员
     *
     * @return 是否是管理员
     */
    public static boolean isAdmin() {
        try {
            // 检查用户类型
            String userType = getUserType();
            if ("admin".equals(userType)) {
                return true;
            }
            
            // 检查角色列表
            List<String> roles = getUserRoles();
            return roles != null && roles.contains("admin");
        } catch (Exception e) {
            log.warn("[SecurityUtils] 检查管理员身份异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断当前是否是VIP用户
     * 注意：管理员也拥有VIP权限
     *
     * @return 是否是VIP用户或管理员
     */
    public static boolean isVip() {
        try {
            // 如果是管理员，也视为VIP
            if (isAdmin()) {
                return true;
            }
            
            // 检查用户类型
            String userType = getUserType();
            if ("vip".equals(userType)) {
                return true;
            }
            
            // 检查角色列表
            List<String> roles = getUserRoles();
            return roles != null && roles.contains("vip");
        } catch (Exception e) {
            log.warn("[SecurityUtils] 检查VIP身份异常: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取用户的权限列表
     * 
     * @return 权限列表
     */
    public static List<String> getPermissions() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getPermissionList();
            }
        } catch (Exception e) {
            log.warn("[SecurityUtils] 获取权限列表异常: {}", e.getMessage());
        }
        
        return List.of();
    }
    
    /**
     * 获取用户的角色列表
     * 
     * @return 角色列表
     */
    public static List<String> getRoles() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getRoleList();
            }
        } catch (Exception e) {
            log.warn("[SecurityUtils] 获取角色列表异常: {}", e.getMessage());
        }
        
        return List.of();
    }
    
    /**
     * 获取当前token
     * 
     * @return token值
     */
    public static String getToken() {
        try {
            return StpUtil.getTokenValue();
        } catch (Exception e) {
            log.warn("[SecurityUtils] 获取token异常: {}", e.getMessage());
            return null;
        }
    }
}