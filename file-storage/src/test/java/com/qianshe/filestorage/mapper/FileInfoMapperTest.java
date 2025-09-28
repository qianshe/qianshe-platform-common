package com.qianshe.filestorage.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.filestorage.entity.FileInfo;
import com.qianshe.filestorage.enums.FileAccessType;
import com.qianshe.filestorage.enums.FileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileInfoMapper测试
 * 
 * @author qianshe
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FileInfoMapperTest {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Test
    void testInsertAndSelect() {
        // 创建测试文件信息
        FileInfo fileInfo = createTestFileInfo();
        
        // 插入
        int result = fileInfoMapper.insert(fileInfo);
        assertEquals(1, result);
        assertNotNull(fileInfo.getId());
        
        // 查询
        FileInfo found = fileInfoMapper.selectById(fileInfo.getId());
        assertNotNull(found);
        assertEquals(fileInfo.getOriginalName(), found.getOriginalName());
        assertEquals(fileInfo.getFileSize(), found.getFileSize());
        assertEquals(fileInfo.getStatus(), found.getStatus());
    }

    @Test
    void testFindByFileHashAndStatus() {
        // 创建并插入测试文件
        FileInfo fileInfo = createTestFileInfo();
        fileInfoMapper.insert(fileInfo);
        
        // 测试查询
        FileInfo found = fileInfoMapper.findByFileHashAndStatus(
                fileInfo.getFileHash(), FileStatus.AVAILABLE);
        assertNotNull(found);
        assertEquals(fileInfo.getId(), found.getId());
        
        // 测试不存在的情况
        FileInfo notFound = fileInfoMapper.findByFileHashAndStatus(
                "nonexistent", FileStatus.AVAILABLE);
        assertNull(notFound);
    }

    @Test
    void testFindByUserIdAndStatusOrderByCreatedAtDesc() {
        // 创建多个测试文件
        FileInfo file1 = createTestFileInfo();
        file1.setUserId(1001L);
        file1.setOriginalName("file1.txt");
        fileInfoMapper.insert(file1);
        
        FileInfo file2 = createTestFileInfo();
        file2.setUserId(1001L);
        file2.setOriginalName("file2.txt");
        file2.setFileHash("hash2");
        fileInfoMapper.insert(file2);
        
        FileInfo file3 = createTestFileInfo();
        file3.setUserId(1002L); // 不同用户
        file3.setOriginalName("file3.txt");
        file3.setFileHash("hash3");
        fileInfoMapper.insert(file3);
        
        // 测试分页查询
        Page<FileInfo> page = new Page<>(1, 10);
        Page<FileInfo> result = fileInfoMapper.findByUserIdAndStatusOrderByCreatedAtDesc(
                page, 1001L, FileStatus.AVAILABLE);
        
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        // 验证是按创建时间倒序排列
        assertTrue(result.getRecords().get(0).getCreatedAt()
                .isAfter(result.getRecords().get(1).getCreatedAt()) ||
                result.getRecords().get(0).getCreatedAt()
                .equals(result.getRecords().get(1).getCreatedAt()));
    }

    @Test
    void testSumFileSizeByUserIdAndStatus() {
        // 创建测试文件
        FileInfo file1 = createTestFileInfo();
        file1.setUserId(1001L);
        file1.setFileSize(1000L);
        fileInfoMapper.insert(file1);
        
        FileInfo file2 = createTestFileInfo();
        file2.setUserId(1001L);
        file2.setFileSize(2000L);
        file2.setFileHash("hash2");
        fileInfoMapper.insert(file2);
        
        // 测试统计
        Long totalSize = fileInfoMapper.sumFileSizeByUserIdAndStatus(1001L, FileStatus.AVAILABLE);
        assertEquals(3000L, totalSize);
        
        // 测试不存在用户
        Long zeroSize = fileInfoMapper.sumFileSizeByUserIdAndStatus(9999L, FileStatus.AVAILABLE);
        assertEquals(0L, zeroSize);
    }

    @Test
    void testIncrementDownloadCount() {
        // 创建测试文件
        FileInfo fileInfo = createTestFileInfo();
        fileInfo.setDownloadCount(5L);
        fileInfoMapper.insert(fileInfo);
        
        // 增加下载次数
        LocalDateTime accessTime = LocalDateTime.now();
        fileInfoMapper.incrementDownloadCount(fileInfo.getId(), accessTime);
        
        // 验证结果
        FileInfo updated = fileInfoMapper.selectById(fileInfo.getId());
        assertEquals(6L, updated.getDownloadCount());
        assertEquals(accessTime.withNano(0), updated.getLastAccessTime().withNano(0));
    }

    @Test
    void testUpdateStatusByIds() {
        // 创建多个测试文件
        FileInfo file1 = createTestFileInfo();
        fileInfoMapper.insert(file1);
        
        FileInfo file2 = createTestFileInfo();
        file2.setFileHash("hash2");
        fileInfoMapper.insert(file2);
        
        // 批量更新状态
        List<Long> ids = Arrays.asList(file1.getId(), file2.getId());
        fileInfoMapper.updateStatusByIds(ids, FileStatus.DELETED);
        
        // 验证更新结果
        FileInfo updated1 = fileInfoMapper.selectById(file1.getId());
        FileInfo updated2 = fileInfoMapper.selectById(file2.getId());
        
        assertEquals(FileStatus.DELETED, updated1.getStatus());
        assertEquals(FileStatus.DELETED, updated2.getStatus());
    }

    @Test
    void testCountByUserIdAndStatus() {
        // 创建测试文件
        FileInfo file1 = createTestFileInfo();
        file1.setUserId(1001L);
        fileInfoMapper.insert(file1);
        
        FileInfo file2 = createTestFileInfo();
        file2.setUserId(1001L);
        file2.setFileHash("hash2");
        fileInfoMapper.insert(file2);
        
        // 测试计数
        Long count = fileInfoMapper.countByUserIdAndStatus(1001L, FileStatus.AVAILABLE);
        assertEquals(2L, count);
        
        // 测试不存在用户
        Long zeroCount = fileInfoMapper.countByUserIdAndStatus(9999L, FileStatus.AVAILABLE);
        assertEquals(0L, zeroCount);
    }

    @Test
    void testCountByUserIdAndStatusAndCreatedAtBetween() {
        // 创建测试文件
        FileInfo fileInfo = createTestFileInfo();
        fileInfo.setUserId(1001L);
        fileInfoMapper.insert(fileInfo);
        
        // 测试时间范围统计
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        
        Long count = fileInfoMapper.countByUserIdAndStatusAndCreatedAtBetween(
                1001L, FileStatus.AVAILABLE, start, end);
        assertEquals(1L, count);
        
        // 测试超出时间范围
        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        
        Long pastCount = fileInfoMapper.countByUserIdAndStatusAndCreatedAtBetween(
                1001L, FileStatus.AVAILABLE, pastStart, pastEnd);
        assertEquals(0L, pastCount);
    }

    /**
     * 创建测试文件信息
     */
    private FileInfo createTestFileInfo() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName("test.txt");
        fileInfo.setStoredName("stored_test.txt");
        fileInfo.setFileSize(1024L);
        fileInfo.setMimeType("text/plain");
        fileInfo.setFileExtension("txt");
        fileInfo.setFileHash("test_hash_" + System.currentTimeMillis());
        fileInfo.setStatus(FileStatus.AVAILABLE);
        fileInfo.setAccessType(FileAccessType.PRIVATE);
        fileInfo.setUserId(1001L);
        fileInfo.setStoragePath("/test/path/test.txt");
        fileInfo.setStorageType("LOCAL");
        fileInfo.setDownloadCount(0L);
        return fileInfo;
    }
}
