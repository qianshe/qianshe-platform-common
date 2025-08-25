package com.qianshe.auth.service.impl;

import com.qianshe.auth.mapper.RoleMapper;
import com.qianshe.auth.service.RoleService;
import com.qianshe.common.exception.ServiceException;
import com.qianshe.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色服务实现类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    
    /**
     * 用户类型与默认角色的映射
     */
    private static final Map<String, String> USER_TYPE_ROLE_MAP = new HashMap<>();
    
    static {
        USER_TYPE_ROLE_MAP.put("user", "user");
        USER_TYPE_ROLE_MAP.put("admin", "admin");
        USER_TYPE_ROLE_MAP.put("vip", "vip");
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return roleMapper.selectUserRoles(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRole(Long userId, String roleCode) {
        // 验证角色是否存在
        if (roleMapper.checkRoleExists(roleCode) == 0) {
            throw new ServiceException(ResultCode.ROLE_NOT_FOUND.getCode(), "角色不存在");
        }
        
        try {
            roleMapper.insertUserRole(userId, roleCode);
            log.info("分配角色成功: userId={}, roleCode={}", userId, roleCode);
        } catch (Exception e) {
            log.error("分配角色失败: userId={}, roleCode={}, error={}", userId, roleCode, e.getMessage(), e);
            throw new ServiceException(ResultCode.ASSIGN_ROLE_FAILED.getCode(), "分配角色失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, String roleCode) {
        try {
            roleMapper.deleteUserRole(userId, roleCode);
            log.info("移除角色成功: userId={}, roleCode={}", userId, roleCode);
        } catch (Exception e) {
            log.error("移除角色失败: userId={}, roleCode={}, error={}", userId, roleCode, e.getMessage(), e);
            throw new ServiceException(ResultCode.REMOVE_ROLE_FAILED.getCode(), "移除角色失败");
        }
    }

    @Override
    public String getDefaultRoleByUserType(String userType) {
        return USER_TYPE_ROLE_MAP.getOrDefault(userType, "user");
    }
} 