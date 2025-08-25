package com.qianshe.auth.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色Mapper接口
 *
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper {

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT role_code FROM sys_user_role WHERE user_id = #{userId}")
    List<String> selectUserRoles(@Param("userId") Long userId);
    
    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     */
    @Insert("INSERT INTO sys_user_role(user_id, role_code) VALUES(#{userId}, #{roleCode})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);
    
    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId} AND role_code = #{roleCode}")
    void deleteUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);
    
    /**
     * 检查角色是否存在
     *
     * @param roleCode 角色编码
     * @return 存在返回1，不存在返回0
     */
    @Select("SELECT COUNT(*) FROM sys_role WHERE code = #{roleCode}")
    int checkRoleExists(@Param("roleCode") String roleCode);
} 