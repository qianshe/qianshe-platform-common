package com.qianshe.auth.domain.vo;

import com.qianshe.common.annotation.ToStringForLong;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详细信息VO
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "用户详细信息VO")
public class UserInfoVO {

    @Schema(description = "用户ID")
    @ToStringForLong
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "用户类型")
    private String userType;
    
    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "状态（0：正常；1：禁用）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 