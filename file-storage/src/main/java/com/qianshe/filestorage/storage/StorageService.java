package com.qianshe.filestorage.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * 存储服务接口
 * 支持本地存储和云存储的统一抽象
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface StorageService {

    /**
     * 存储文件
     * 
     * @param file 上传的文件
     * @param storagePath 存储路径
     * @return 实际存储路径
     * @throws StorageException 存储异常
     */
    String store(MultipartFile file, String storagePath) throws StorageException;

    /**
     * 存储文件流
     * 
     * @param inputStream 文件输入流
     * @param storagePath 存储路径
     * @param contentLength 内容长度
     * @param contentType 内容类型
     * @return 实际存储路径
     * @throws StorageException 存储异常
     */
    String store(InputStream inputStream, String storagePath, long contentLength, String contentType) throws StorageException;

    /**
     * 读取文件
     * 
     * @param storagePath 存储路径
     * @return 文件输入流
     * @throws StorageException 存储异常
     */
    InputStream load(String storagePath) throws StorageException;

    /**
     * 删除文件
     * 
     * @param storagePath 存储路径
     * @return 是否删除成功
     * @throws StorageException 存储异常
     */
    boolean delete(String storagePath) throws StorageException;

    /**
     * 检查文件是否存在
     * 
     * @param storagePath 存储路径
     * @return 是否存在
     * @throws StorageException 存储异常
     */
    boolean exists(String storagePath) throws StorageException;

    /**
     * 获取文件大小
     * 
     * @param storagePath 存储路径
     * @return 文件大小（字节）
     * @throws StorageException 存储异常
     */
    long getFileSize(String storagePath) throws StorageException;

    /**
     * 获取文件访问URL
     * 
     * @param storagePath 存储路径
     * @return 访问URL
     * @throws StorageException 存储异常
     */
    String getAccessUrl(String storagePath) throws StorageException;

    /**
     * 获取文件预签名URL（用于直接上传/下载）
     * 
     * @param storagePath 存储路径
     * @param expireSeconds 过期时间（秒）
     * @return 预签名URL
     * @throws StorageException 存储异常
     */
    String getPresignedUrl(String storagePath, int expireSeconds) throws StorageException;

    /**
     * 复制文件
     * 
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 是否复制成功
     * @throws StorageException 存储异常
     */
    boolean copy(String sourcePath, String targetPath) throws StorageException;

    /**
     * 移动文件
     * 
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 是否移动成功
     * @throws StorageException 存储异常
     */
    boolean move(String sourcePath, String targetPath) throws StorageException;

    /**
     * 获取存储类型
     * 
     * @return 存储类型
     */
    String getStorageType();

    /**
     * 初始化存储服务
     * 
     * @throws StorageException 存储异常
     */
    void init() throws StorageException;

    /**
     * 销毁存储服务
     * 
     * @throws StorageException 存储异常
     */
    void destroy() throws StorageException;
}
