package com.qianshe.auth.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianshe.auth.domain.dto.LoginDTO;
import com.qianshe.auth.domain.dto.RegisterDTO;
import com.qianshe.auth.domain.dto.UpdatePasswordDTO;
import com.qianshe.auth.domain.vo.LoginVO;
import com.qianshe.auth.domain.vo.UserInfoVO;
import com.qianshe.auth.mapper.UserMapper;
import com.qianshe.auth.service.CaptchaService;
import com.qianshe.auth.service.RoleService;
import com.qianshe.auth.service.UserService;
import com.qianshe.common.domain.User;
import com.qianshe.common.exception.ServiceException;
import com.qianshe.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Service实现类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final CaptchaService captchaService;
    private final StpLogic userStpLogic;
    private final StpLogic adminStpLogic;
    private final StpLogic vipStpLogic;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        log.info("开始用户注册，用户名: {}", registerDTO.getUsername());

        // 1. 验证图形验证码
        boolean captchaValid = captchaService.validateCaptcha(
            registerDTO.getCaptchaKey(),
            registerDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            log.warn("用户注册失败，验证码验证失败: {}", registerDTO.getUsername());
            throw new ServiceException(ResultCode.VERIFY_CODE_ERROR.getCode(), ResultCode.VERIFY_CODE_ERROR.getMessage());
        }
        log.debug("验证码验证通过，用户名: {}", registerDTO.getUsername());

        // 2. 校验密码
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new ServiceException(ResultCode.PASSWORD_NOT_MATCH.getCode(), ResultCode.PASSWORD_NOT_MATCH.getMessage());
        }

        // 3. 校验用户名唯一性
        checkUsernameUnique(registerDTO.getUsername());
        log.debug("用户名唯一性校验通过: {}", registerDTO.getUsername());

        // 4. 校验手机号唯一性
        if (StrUtil.isNotBlank(registerDTO.getMobile())) {
            checkMobileUnique(registerDTO.getMobile());
            log.debug("手机号唯一性校验通过: {}", registerDTO.getMobile());
        }

        // 5. 校验邮箱唯一性
        if (StrUtil.isNotBlank(registerDTO.getEmail())) {
            checkEmailUnique(registerDTO.getEmail());
            log.debug("邮箱唯一性校验通过: {}", registerDTO.getEmail());
        }

        // 6. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setNickname(StrUtil.isNotBlank(registerDTO.getNickname()) ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setMobile(registerDTO.getMobile());
        user.setEmail(registerDTO.getEmail());
        user.setUserType("user"); // 默认为普通用户
        user.setStatus(0);
        log.debug("用户信息构建完成: {}", registerDTO.getUsername());

        try {
            // 7. 保存用户到数据库
            save(user);
            log.debug("用户数据保存成功: {}", user.getUsername());

            // 8. 分配默认角色
            roleService.assignUserRole(user.getId(), roleService.getDefaultRoleByUserType(user.getUserType()));
            log.debug("默认角色分配成功: {}", user.getUsername());

            log.info("用户注册成功: {}, ID: {}", user.getUsername(), user.getId());
        } catch (Exception e) {
            log.error("用户注册失败: {}, 错误: {}", registerDTO.getUsername(), e.getMessage(), e);
            throw new ServiceException(ResultCode.REGISTER_FAILED.getCode(), ResultCode.REGISTER_FAILED.getMessage());
        }
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 校验登录类型
        String loginType = loginDTO.getLoginType();
        if (!isValidLoginType(loginType)) {
            throw new ServiceException(ResultCode.INVALID_LOGIN_TYPE.getCode(), ResultCode.INVALID_LOGIN_TYPE.getMessage());
        }

        // 根据登录类型和账号查询用户
        User user = getUserByLoginType(loginDTO);
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", loginDTO.getAccount());
            throw new ServiceException(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }

        // 校验账号状态
        if (user.getStatus() == 1) {
            log.warn("登录失败，账号已被禁用: {}", loginDTO.getAccount());
            throw new ServiceException(ResultCode.ACCOUNT_DISABLED.getCode(), ResultCode.ACCOUNT_DISABLED.getMessage());
        }

        // 校验凭证
        validateCredential(loginDTO, user);
        
        // 校验用户类型，如果指定了用户类型，则需要验证用户是否有该类型
        String userType = loginDTO.getUserType();
        if (StrUtil.isNotBlank(userType) && !userType.equals(user.getUserType()) && !"admin".equals(user.getUserType())) {
            log.warn("登录失败，无权使用该用户类型登录: {}, userType: {}", loginDTO.getAccount(), userType);
            throw new ServiceException(ResultCode.UNAUTHORIZED_USER_TYPE.getCode(), ResultCode.UNAUTHORIZED_USER_TYPE.getMessage());
        }
        
        // 获取用户角色
        List<String> roles = roleService.getUserRoles(user.getId());
        
        // 根据用户类型选择对应的StpLogic进行登录
        StpLogic stpLogic = selectStpLogicByUserType(user.getUserType());
        
        // 生成token，设置登录设备类型
        stpLogic.login(user.getId(), SaLoginModel.create()
                .setDevice("web")                // 此次登录的客户端设备标识
                .setIsLastingCookie(true)        // 是否为持久Cookie
                .setTimeout(60 * 60 * 24 * 30)   // 指定此次登录token的有效期: 30天
        );
        
        // 在Session中存储用户信息
        stpLogic.getSession().set("roles", roles);
        stpLogic.getSession().set("userType", user.getUserType());
        
        String token = stpLogic.getTokenValue();
        log.info("用户登录成功: {}, userType: {}", user.getUsername(), user.getUserType());

        // 返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setEmail(user.getEmail());
        loginVO.setToken(token);
        loginVO.setRoles(roles);
        loginVO.setUserType(user.getUserType());

        return loginVO;
    }
    
    /**
     * 根据用户类型选择对应的StpLogic
     */
    private StpLogic selectStpLogicByUserType(String userType) {
        return switch (userType) {
            case "admin" -> adminStpLogic;
            case "vip" -> vipStpLogic;
            default -> userStpLogic;
        };
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        // 校验新密码与确认密码是否一致
        if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmNewPassword())) {
            throw new ServiceException(ResultCode.PASSWORD_NOT_MATCH.getCode(), ResultCode.PASSWORD_NOT_MATCH.getMessage());
        }

        // 获取当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();

        // 获取用户信息
        User user = getById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 校验旧密码
        if (!BCrypt.checkpw(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new ServiceException(ResultCode.PASSWORD_ERROR.getCode(), "旧密码错误");
        }
        
        // 更新密码
        user.setPassword(BCrypt.hashpw(updatePasswordDTO.getNewPassword()));
        updateById(user);
        log.info("用户[{}]密码修改成功", user.getUsername());
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        return getUserInfoById(userId);
    }


    @Override
    public UserInfoVO getUserInfoById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 获取用户角色
        List<String> roles = roleService.getUserRoles(userId);
        
        // 转换为VO对象
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setMobile(user.getMobile());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setUserType(user.getUserType());
        userInfoVO.setRoles(roles);
        userInfoVO.setStatus(user.getStatus());
        userInfoVO.setCreateTime(user.getCreateTime());
        userInfoVO.setUpdateTime(user.getUpdateTime());
        
        return userInfoVO;
    }

    /**
     * 校验用户名唯一性
     */
    private void checkUsernameUnique(String username) {
        if (getByUsername(username) != null) {
            throw new ServiceException(ResultCode.USERNAME_ALREADY_EXISTS.getCode(), ResultCode.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }

    /**
     * 校验手机号唯一性
     */
    private void checkMobileUnique(String mobile) {
        if (getByMobile(mobile) != null) {
            throw new ServiceException(ResultCode.MOBILE_ALREADY_EXISTS.getCode(), ResultCode.MOBILE_ALREADY_EXISTS.getMessage());
        }
    }

    /**
     * 校验邮箱唯一性
     */
    private void checkEmailUnique(String email) {
        if (getByEmail(email) != null) {
            throw new ServiceException(ResultCode.EMAIL_ALREADY_EXISTS.getCode(), ResultCode.EMAIL_ALREADY_EXISTS.getMessage());
        }
    }

    /**
     * 校验登录类型是否有效
     */
    private boolean isValidLoginType(String loginType) {
        return "1".equals(loginType) || "2".equals(loginType) || "3".equals(loginType);
    }

    /**
     * 根据登录类型获取用户
     */
    private User getUserByLoginType(LoginDTO loginDTO) {
        return switch (loginDTO.getLoginType()) {
            case "1" -> getByUsername(loginDTO.getAccount());
            case "2" -> getByMobile(loginDTO.getAccount());
            case "3" -> getByEmail(loginDTO.getAccount());
            default -> null;
        };
    }

    /**
     * 校验登录凭证
     */
    private void validateCredential(LoginDTO loginDTO, User user) {
        switch (loginDTO.getLoginType()) {
            case "1", "3" -> {
                // 用户名密码、邮箱密码登录
                if (!BCrypt.checkpw(loginDTO.getCredential(), user.getPassword())) {
                    log.warn("登录失败，密码错误: {}", loginDTO.getAccount());
                    throw new ServiceException(ResultCode.PASSWORD_ERROR.getCode(), ResultCode.PASSWORD_ERROR.getMessage());
                }
            }
            case "2" -> {
                // 手机号验证码登录
                // TODO: 验证码校验，待实现
                log.info("手机号验证码登录，跳过验证码校验: {}", loginDTO.getAccount());
            }
        }
    }
} 