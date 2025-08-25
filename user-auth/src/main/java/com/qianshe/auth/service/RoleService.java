package com.qianshe.auth.service;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author qianshe
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     */
    void assignUserRole(Long userId, String roleCode);
    
    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     */
    void removeUserRole(Long userId, String roleCode);
    
    /**
     * 根据用户类型获取默认角色
     *
     * @param userType 用户类型
     * @return 默认角色编码
     */
    String getDefaultRoleByUserType(String userType);
} 