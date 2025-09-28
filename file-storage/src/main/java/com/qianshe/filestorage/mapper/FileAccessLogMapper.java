package com.qianshe.filestorage.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.filestorage.entity.FileAccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件访问日志Mapper
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface FileAccessLogMapper extends BaseMapper<FileAccessLog> {

    /**
     * 根据文件ID分页查询访问日志
     *
     * @param page 分页参数
     * @param fileId 文件ID
     * @return 访问日志列表
     */
    default Page<FileAccessLog> findByFileIdOrderByCreatedAtDesc(Page<FileAccessLog> page, @Param("fileId") Long fileId) {
        return selectPage(page, new QueryWrapper<FileAccessLog>()
                .eq("file_id", fileId)
                .orderByDesc("created_at"));
    }

    /**
     * 根据用户ID分页查询访问日志
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 访问日志列表
     */
    default Page<FileAccessLog> findByUserIdOrderByCreatedAtDesc(Page<FileAccessLog> page, @Param("userId") Long userId) {
        return selectPage(page, new QueryWrapper<FileAccessLog>()
                .eq("user_id", userId)
                .orderByDesc("created_at"));
    }

    /**
     * 根据操作类型查询访问日志
     *
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 访问日志列表
     */
    default List<FileAccessLog> findByActionAndCreatedAtBetween(@Param("action") String action,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime) {
        return selectList(new QueryWrapper<FileAccessLog>()
                .eq("action", action)
                .between("created_at", startTime, endTime)
                .orderByDesc("created_at"));
    }

    /**
     * 统计文件访问次数
     * 
     * @param fileId 文件ID
     * @param action 操作类型
     * @return 访问次数
     */
    @Select("SELECT COUNT(*) FROM file_access_log WHERE file_id = #{fileId} AND action = #{action}")
    Long countByFileIdAndAction(@Param("fileId") Long fileId, @Param("action") String action);

    /**
     * 统计用户操作次数
     * 
     * @param userId 用户ID
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    @Select("SELECT COUNT(*) FROM file_access_log WHERE user_id = #{userId} AND action = #{action} " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}")
    Long countByUserIdAndActionAndCreatedAtBetween(@Param("userId") Long userId,
                                                  @Param("action") String action,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最近的访问日志
     *
     * @param fileId 文件ID
     * @param action 操作类型
     * @return 最近的访问日志
     */
    default FileAccessLog findTopByFileIdAndActionOrderByCreatedAtDesc(@Param("fileId") Long fileId,
                                                              @Param("action") String action) {
        return selectOne(new QueryWrapper<FileAccessLog>()
                .eq("file_id", fileId)
                .eq("action", action)
                .orderByDesc("created_at")
                .last("LIMIT 1"));
    }

    /**
     * 删除过期的访问日志
     *
     * @param expireTime 过期时间
     * @return 删除的记录数
     */
    default int deleteByCreatedAtBefore(@Param("expireTime") LocalDateTime expireTime) {
        return delete(new QueryWrapper<FileAccessLog>()
                .lt("created_at", expireTime));
    }

    /**
     * 统计时间范围内的访问量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 访问量
     */
    @Select("SELECT COUNT(*) FROM file_access_log WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    Long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计错误访问日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误访问日志列表
     */
    default List<FileAccessLog> findErrorLogsByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime) {
        return selectList(new QueryWrapper<FileAccessLog>()
                .between("created_at", startTime, endTime)
                .and(wrapper -> wrapper
                        .isNull("response_status")
                        .or()
                        .ge("response_status", 400)
                        .or()
                        .isNotNull("error_message"))
                .orderByDesc("created_at"));
    }
}
