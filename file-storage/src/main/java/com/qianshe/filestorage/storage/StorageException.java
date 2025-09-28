package com.qianshe.filestorage.storage;

/**
 * 存储异常
 * 
 * @author qianshe
 * @since 1.0.0
 */
public class StorageException extends Exception {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }
}
