package com.qianshe.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qianshe.auth.domain.dto.LoginDTO;
import com.qianshe.auth.domain.dto.RegisterDTO;
import com.qianshe.auth.domain.dto.UpdatePasswordDTO;
import com.qianshe.auth.domain.vo.LoginVO;
import com.qianshe.auth.domain.vo.UserInfoVO;
import com.qianshe.common.domain.User;

/**
 * 用户Service接口
 *
 * @author qianshe
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 包含图形验证码验证功能，确保注册安全性
     *
     * @param registerDTO 注册信息，包含验证码标识和验证码
     * @throws ServiceException 当验证码验证失败或其他注册条件不满足时抛出异常
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    User getByMobile(String mobile);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getByEmail(String email);
    
    /**
     * 修改密码
     *
     * @param updatePasswordDTO 修改密码信息
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO);
    
    /**
     * 获取当前登录用户信息
     *
     * @return 用户详细信息
     */
    UserInfoVO getCurrentUserInfo();
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    UserInfoVO getUserInfoById(Long userId);
} 