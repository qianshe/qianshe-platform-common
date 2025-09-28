package com.qianshe.filestorage.repository;

import com.qianshe.filestorage.entity.FileInfo;
import com.qianshe.filestorage.enums.FileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文件信息Repository
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    /**
     * 根据文件哈希查找文件
     * 
     * @param fileHash 文件哈希
     * @return 文件信息
     */
    Optional<FileInfo> findByFileHashAndStatus(String fileHash, FileStatus status);

    /**
     * 根据用户ID分页查询文件列表
     * 
     * @param userId 用户ID
     * @param status 文件状态
     * @param pageable 分页参数
     * @return 文件列表
     */
    Page<FileInfo> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, FileStatus status, Pageable pageable);

    /**
     * 根据业务类型和业务ID查询文件列表
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param status 文件状态
     * @return 文件列表
     */
    List<FileInfo> findByBusinessTypeAndBusinessIdAndStatus(String businessType, String businessId, FileStatus status);

    /**
     * 查询过期文件
     * 
     * @param expireTime 过期时间
     * @param status 文件状态
     * @return 过期文件列表
     */
    List<FileInfo> findByExpireTimeBeforeAndStatus(LocalDateTime expireTime, FileStatus status);

    /**
     * 查询长时间未访问的文件
     * 
     * @param lastAccessTime 最后访问时间
     * @param status 文件状态
     * @return 文件列表
     */
    List<FileInfo> findByLastAccessTimeBeforeAndStatus(LocalDateTime lastAccessTime, FileStatus status);

    /**
     * 统计用户文件总大小
     * 
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileInfo f WHERE f.userId = :userId AND f.status = :status")
    Long sumFileSizeByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FileStatus status);

    /**
     * 更新文件下载次数
     * 
     * @param id 文件ID
     * @param lastAccessTime 最后访问时间
     */
    @Modifying
    @Query("UPDATE FileInfo f SET f.downloadCount = f.downloadCount + 1, f.lastAccessTime = :lastAccessTime WHERE f.id = :id")
    void incrementDownloadCount(@Param("id") Long id, @Param("lastAccessTime") LocalDateTime lastAccessTime);

    /**
     * 批量更新文件状态
     * 
     * @param ids 文件ID列表
     * @param status 新状态
     */
    @Modifying
    @Query("UPDATE FileInfo f SET f.status = :status WHERE f.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") FileStatus status);

    /**
     * 根据存储路径查找文件
     *
     * @param storagePath 存储路径
     * @return 文件信息
     */
    Optional<FileInfo> findByStoragePath(String storagePath);

    /**
     * 统计用户文件数量
     *
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件数量
     */
    Long countByUserIdAndStatus(Long userId, FileStatus status);

    /**
     * 根据时间范围统计用户文件数量
     *
     * @param userId 用户ID
     * @param status 文件状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 文件数量
     */
    Long countByUserIdAndStatusAndCreatedAtBetween(Long userId, FileStatus status,
                                                   LocalDateTime startTime, LocalDateTime endTime);
}
