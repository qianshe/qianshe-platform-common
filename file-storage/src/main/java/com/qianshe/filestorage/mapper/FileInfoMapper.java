package com.qianshe.filestorage.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.filestorage.entity.FileInfo;
import com.qianshe.filestorage.enums.FileStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息Mapper
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据文件哈希和状态查找文件
     *
     * @param fileHash 文件哈希
     * @param status 文件状态
     * @return 文件信息
     */
    default FileInfo findByFileHashAndStatus(@Param("fileHash") String fileHash, @Param("status") FileStatus status) {
        return selectOne(new QueryWrapper<FileInfo>()
                .eq("file_hash", fileHash)
                .eq("status", status)
                .last("LIMIT 1"));
    }

    /**
     * 根据用户ID分页查询文件列表（按创建时间倒序）
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件列表
     */
    default Page<FileInfo> findByUserIdAndStatusOrderByCreatedAtDesc(Page<FileInfo> page,
                                                           @Param("userId") Long userId,
                                                           @Param("status") FileStatus status) {
        return selectPage(page, new QueryWrapper<FileInfo>()
                .eq("user_id", userId)
                .eq("status", status)
                .orderByDesc("created_at"));
    }

    /**
     * 根据业务类型和业务ID查询文件列表
     *
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param status 文件状态
     * @return 文件列表
     */
    default List<FileInfo> findByBusinessTypeAndBusinessIdAndStatus(@Param("businessType") String businessType,
                                                           @Param("businessId") String businessId,
                                                           @Param("status") FileStatus status) {
        return selectList(new QueryWrapper<FileInfo>()
                .eq("business_type", businessType)
                .eq("business_id", businessId)
                .eq("status", status)
                .orderByDesc("created_at"));
    }

    /**
     * 查询过期文件
     *
     * @param expireTime 过期时间
     * @param status 文件状态
     * @return 过期文件列表
     */
    default List<FileInfo> findByExpireTimeBeforeAndStatus(@Param("expireTime") LocalDateTime expireTime,
                                                  @Param("status") FileStatus status) {
        return selectList(new QueryWrapper<FileInfo>()
                .lt("expire_time", expireTime)
                .eq("status", status)
                .orderByAsc("expire_time"));
    }

    /**
     * 查询长时间未访问的文件
     *
     * @param lastAccessTime 最后访问时间
     * @param status 文件状态
     * @return 文件列表
     */
    default List<FileInfo> findByLastAccessTimeBeforeAndStatus(@Param("lastAccessTime") LocalDateTime lastAccessTime,
                                                      @Param("status") FileStatus status) {
        return selectList(new QueryWrapper<FileInfo>()
                .lt("last_access_time", lastAccessTime)
                .eq("status", status)
                .orderByAsc("last_access_time"));
    }

    /**
     * 统计用户文件总大小
     * 
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件总大小
     */
    @Select("SELECT COALESCE(SUM(file_size), 0) FROM file_info WHERE user_id = #{userId} AND status = #{status}")
    Long sumFileSizeByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FileStatus status);

    /**
     * 更新文件下载次数
     * 
     * @param id 文件ID
     * @param lastAccessTime 最后访问时间
     */
    @Update("UPDATE file_info SET download_count = download_count + 1, last_access_time = #{lastAccessTime} WHERE id = #{id}")
    void incrementDownloadCount(@Param("id") Long id, @Param("lastAccessTime") LocalDateTime lastAccessTime);

    /**
     * 批量更新文件状态
     *
     * @param ids 文件ID列表
     * @param status 新状态
     */
    default void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") FileStatus status) {
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(id -> {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(id);
                fileInfo.setStatus(status);
                updateById(fileInfo);
            });
        }
    }

    /**
     * 根据存储路径查找文件
     *
     * @param storagePath 存储路径
     * @return 文件信息
     */
    default FileInfo findByStoragePath(@Param("storagePath") String storagePath) {
        return selectOne(new QueryWrapper<FileInfo>()
                .eq("storage_path", storagePath)
                .last("LIMIT 1"));
    }

    /**
     * 统计用户文件数量
     *
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件数量
     */
    @Select("SELECT COUNT(*) FROM file_info WHERE user_id = #{userId} AND status = #{status}")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FileStatus status);

    /**
     * 根据时间范围统计用户文件数量
     *
     * @param userId 用户ID
     * @param status 文件状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 文件数量
     */
    @Select("SELECT COUNT(*) FROM file_info WHERE user_id = #{userId} AND status = #{status} " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}")
    Long countByUserIdAndStatusAndCreatedAtBetween(@Param("userId") Long userId, 
                                                  @Param("status") FileStatus status,
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
}
