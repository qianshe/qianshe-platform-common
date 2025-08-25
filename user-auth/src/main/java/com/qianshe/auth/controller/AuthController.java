package com.qianshe.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.qianshe.auth.domain.dto.LoginDTO;
import com.qianshe.auth.domain.dto.RegisterDTO;
import com.qianshe.auth.domain.dto.UpdatePasswordDTO;
import com.qianshe.auth.domain.vo.LoginVO;
import com.qianshe.auth.domain.vo.UserInfoVO;
import com.qianshe.auth.service.UserService;
import com.qianshe.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author qianshe
 * @since 1.0.0
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final UserService userService;
    private final StpLogic userStpLogic;
    private final StpLogic adminStpLogic;
    private final StpLogic vipStpLogic;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.ok();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.ok(loginVO);
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login/admin")
    public Result<LoginVO> adminLogin(@Validated @RequestBody LoginDTO loginDTO) {
        // 设置用户类型为admin
        loginDTO.setUserType("admin");
        LoginVO loginVO = userService.login(loginDTO);
        return Result.ok(loginVO);
    }

    @Operation(summary = "VIP用户登录")
    @PostMapping("/login/vip")
    public Result<LoginVO> vipLogin(@Validated @RequestBody LoginDTO loginDTO) {
        // 设置用户类型为vip
        loginDTO.setUserType("vip");
        LoginVO loginVO = userService.login(loginDTO);
        return Result.ok(loginVO);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    @SaCheckLogin
    public Result<Void> logout() {
        // 获取当前登录的用户类型
        String userType = StpUtil.getLoginIdDefaultNull() != null ? "user" : 
                          adminStpLogic.getLoginIdDefaultNull() != null ? "admin" : 
                          vipStpLogic.getLoginIdDefaultNull() != null ? "vip" : null;
        
        // 根据用户类型选择对应的StpLogic进行登出
        switch (userType) {
            case "admin" -> adminStpLogic.logout();
            case "vip" -> vipStpLogic.logout();
            default -> userStpLogic.logout();
        }
        
        return Result.ok();
    }

    @Operation(summary = "查询是否登录")
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin() {
        // 检查任一账号类型是否登录
        boolean isLogin = StpUtil.isLogin() || adminStpLogic.isLogin() || vipStpLogic.isLogin();
        return Result.ok(isLogin);
    }
    
    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    @SaCheckLogin
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(updatePasswordDTO);
        return Result.ok();
    }
    
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getCurrentUserInfo")
    @SaCheckLogin
    public Result<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfoVO = userService.getCurrentUserInfo();
        return Result.ok(userInfoVO);
    }
    
    @Operation(summary = "根据用户ID获取用户信息")
    @GetMapping("/getUserInfo/{userId}")
    @SaCheckLogin
    public Result<UserInfoVO> getUserInfo(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        UserInfoVO userInfoVO = userService.getUserInfoById(userId);
        return Result.ok(userInfoVO);
    }
    
    @Operation(summary = "管理员专用API")
    @GetMapping("/admin/test")
    @SaCheckRole("admin")
    public Result<String> adminTest() {
        return Result.ok("这是一个管理员专用API");
    }
    
    @Operation(summary = "VIP用户专用API")
    @GetMapping("/vip/test")
    @SaCheckRole("vip")
    public Result<String> vipTest() {
        return Result.ok("这是一个VIP用户专用API");
    }
} 