package com.qianshe.filestorage.dto;

import com.qianshe.filestorage.enums.FileAccessType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文件上传请求DTO
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {

    /**
     * 上传的文件
     */
    @NotNull(message = "文件不能为空")
    @Schema(description = "上传的文件", required = true)
    private MultipartFile file;

    /**
     * 访问权限类型
     */
    @Schema(description = "访问权限类型", example = "PRIVATE")
    private FileAccessType accessType = FileAccessType.PRIVATE;

    /**
     * 业务类型
     */
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    @Schema(description = "业务类型", example = "avatar")
    private String businessType;

    /**
     * 业务ID
     */
    @Size(max = 100, message = "业务ID长度不能超过100个字符")
    @Schema(description = "业务ID", example = "user_123")
    private String businessId;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 扩展数据
     */
    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;

    /**
     * 是否覆盖同名文件
     */
    @Schema(description = "是否覆盖同名文件", example = "false")
    private Boolean overwrite = false;

    /**
     * 是否生成缩略图（仅图片文件）
     */
    @Schema(description = "是否生成缩略图", example = "true")
    private Boolean generateThumbnail = true;

    /**
     * 缩略图尺寸（格式：width,height，多个用分号分隔）
     */
    @Schema(description = "缩略图尺寸", example = "100,100;200,200;400,400")
    private String thumbnailSizes;
}
