package com.qianshe.auth.domain.vo;

import com.qianshe.common.annotation.ToStringForLong;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录返回VO
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "登录返回VO")
public class LoginVO {

    @Schema(description = "用户ID")
    @ToStringForLong
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户类型")
    private String userType;
    
    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "token")
    private String token;
} 