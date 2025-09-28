package com.qianshe.filestorage.repository;

import com.qianshe.filestorage.entity.FileAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件访问日志Repository
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Repository
public interface FileAccessLogRepository extends JpaRepository<FileAccessLog, Long> {

    /**
     * 根据文件ID分页查询访问日志
     * 
     * @param fileId 文件ID
     * @param pageable 分页参数
     * @return 访问日志列表
     */
    Page<FileAccessLog> findByFileIdOrderByCreatedAtDesc(Long fileId, Pageable pageable);

    /**
     * 根据用户ID分页查询访问日志
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 访问日志列表
     */
    Page<FileAccessLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 统计文件访问次数
     * 
     * @param fileId 文件ID
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 访问次数
     */
    @Query("SELECT COUNT(l) FROM FileAccessLog l WHERE l.fileId = :fileId AND l.action = :action AND l.createdAt BETWEEN :startTime AND :endTime")
    Long countByFileIdAndActionAndCreatedAtBetween(@Param("fileId") Long fileId, 
                                                   @Param("action") String action,
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户操作次数
     * 
     * @param userId 用户ID
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    @Query("SELECT COUNT(l) FROM FileAccessLog l WHERE l.userId = :userId AND l.action = :action AND l.createdAt BETWEEN :startTime AND :endTime")
    Long countByUserIdAndActionAndCreatedAtBetween(@Param("userId") Long userId, 
                                                   @Param("action") String action,
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 查询热门文件（按下载次数排序）
     * 
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 文件ID列表
     */
    @Query("SELECT l.fileId, COUNT(l) as accessCount FROM FileAccessLog l WHERE l.action = :action AND l.createdAt BETWEEN :startTime AND :endTime GROUP BY l.fileId ORDER BY accessCount DESC")
    Page<Object[]> findPopularFiles(@Param("action") String action,
                                    @Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime,
                                    Pageable pageable);

    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 时间点
     * @return 删除的记录数
     */
    Long deleteByCreatedAtBefore(LocalDateTime beforeTime);
}
