package io.github.yanshenwei.cos;

import io.github.yanshenwei.cos.config.CosConstants;
import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**********************************
 * @Author YSW
 * @Description
 * @Date 2022/10/28 - 15:45
 * 存储桶操作 	    对象操作 	    Presigned操作 	        存储桶策略
 * makeBucket 	    getObject 	    presignedGetObject 	    getBucketPolicy
 * listBuckets 	    putObject 	    presignedPutObject 	    setBucketPolicy
 * bucketExists 	copyObject 	    presignedPostPolicy
 * removeBucket 	statObject
 * listObjects 	    removeObject
 * listIncompleteUploads 	removeIncompleteUpload
 **********************************/

@Component
@ConditionalOnProperty(prefix = "cos.minio", name = "enable", havingValue = "true")
public class MinioModel implements ObjectCloudStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioModel.class);

    private static final String FILE_NOT_EXIST = "Object does not exist";
    private static final String KEY_NOT_EXIST = "The specified key does not exist.";

    @Resource
    private CosConstants.MinioConfig minioConfig;

    private MinioClient minioClient;

    private String bucket;

    private String objectPrefix;

    @PostConstruct
    private void init() {
        final String objectDirPrefix = minioConfig.getObjectDirPrefix();
        if (objectDirPrefix != null && objectDirPrefix.trim().length() > 0) {
            //noinspection AlibabaUndefineMagicConstant
            if (!"/".endsWith(objectDirPrefix)) {
                this.objectPrefix = objectDirPrefix + "/";
            } else //noinspection AlibabaUndefineMagicConstant
                if ("/".equals(objectDirPrefix.trim())) {
                this.objectPrefix = "";
            } else {
                this.objectPrefix = objectDirPrefix;
            }
        } else {
            this.objectPrefix = "";
        }
        log.debug("minio.object-dir-prefix: " + objectPrefix);
        bucket = minioConfig.getBucket();
        minioClient = MinioClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKeyId(), minioConfig.getAccessKeySecret())
                .build();
    }

    public MinioClient getMinio() {
        return this.minioClient;
    }

    /**
     * 上传对象
     *
     * @param objectPath 对象名称
     * @param file       对象文件
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, File file) {
        MinioClient minio = getMinio();
        try {
            minio.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            if (FILE_NOT_EXIST.equals(e.getMessage())) {
                final PutObjectArgs putObjectArgs;
                try (InputStream inputStream = new FileInputStream(file)) {
                    putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("application/octet-stream")
                            .build();
                    minio.putObject(putObjectArgs);
                    log.debug("对象 [" + objectPrefix + objectPath + "] 上传成功");
                    return true;
                } catch (ErrorResponseException | InternalException |
                        XmlParserException | InsufficientDataException |
                        InvalidKeyException | InvalidResponseException |
                        NoSuchAlgorithmException | ServerException |
                        IOException e1) {
                    log.error("对象 [" + objectPrefix + objectPath + "] 上传异常");
                }
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 上传流对象
     *
     * @param objectPath  对象名称
     * @param inputStream 字节数流对象
     * @param contentType 对象头类型
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, InputStream inputStream, String contentType) {
        MinioClient minio = getMinio();
        try {
            minio.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            if (FILE_NOT_EXIST.equals(e.getMessage())) {
                try {
                    final PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build();
                    minio.putObject(putObjectArgs);
                    log.debug("对象 [" + objectPath + "] 上传成功");
                    return true;
                } catch (ErrorResponseException | InternalException |
                        XmlParserException | InsufficientDataException |
                        InvalidKeyException | InvalidResponseException |
                        NoSuchAlgorithmException | ServerException |
                        IOException e1) {
                    log.error("对象 [" + objectPrefix + objectPath + "] 上传异常");
                }
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 上传字节数组对象
     *
     * @param objectPath 对象名称
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, byte[] content) {
        MinioClient minio = getMinio();
        try {
            minio.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            return false;
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            if (FILE_NOT_EXIST.equals(e.getMessage())) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(content);
                    final PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("application/octet-stream")
                            .build();
                    minio.putObject(putObjectArgs);
                    log.debug("对象 [" + objectPrefix + objectPath + "] 上传成功");
                    return true;
                } catch (ErrorResponseException | InternalException |
                        XmlParserException | InsufficientDataException |
                        InvalidKeyException | InvalidResponseException |
                        NoSuchAlgorithmException | ServerException |
                        IOException e1) {
                    log.error("对象 [" + objectPrefix + objectPath + "] 上传异常");
                }
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 上传字节数组对象
     *
     * @param objectPath  对象名称
     * @param content     对象字节数组
     * @param contentType 对象头类型
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, byte[] content, String contentType) {
        MinioClient minio = getMinio();
        try {
            minio.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            if (FILE_NOT_EXIST.equals(e.getMessage())) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(content);
                    final PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build();
                    minio.putObject(putObjectArgs);
                    log.debug("对象 [" + objectPath + "] 上传成功");
                    return true;
                } catch (ErrorResponseException | InternalException |
                        XmlParserException | InsufficientDataException |
                        InvalidKeyException | InvalidResponseException |
                        NoSuchAlgorithmException | ServerException |
                        IOException e1) {
                    log.error("对象 [" + objectPath + "] 上传异常");
                }
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 上传可追加字节流对象
     *
     * @param objectPath 对象名称
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean pudAppendableObject(String objectPath, byte[] content) {
        return putObject(objectPath, content);
    }

    /**
     * 对象追加内容
     *
     * @param objectPath 对象名称
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean appendObject(String objectPath, byte[] content) {
        MinioClient minio = getMinio();
        final CosObject object = getObject(objectPath);
        if (object != null) {
            try {
                final long oldLength = object.getContentLength();
                if (oldLength >= Integer.MAX_VALUE) {
                    log.error("对象 [" + objectPrefix + objectPath + "] 太大,无法追加");
                    return false;
                }
                final byte[] bytes = new byte[Math.toIntExact(oldLength + content.length)];
                //noinspection ResultOfMethodCallIgnored
                object.getInputStream().read(bytes);
                object.getInputStream().close();
                System.arraycopy(content, 0, bytes, Math.toIntExact(oldLength), content.length);
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                final PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                        .object(objectPrefix + objectPath)
                        .stream(inputStream, inputStream.available(), -1)
                        .build();
                minio.putObject(putObjectArgs);
                log.debug("对象 [" + objectPrefix + objectPath + "] 追加成功 Length " + oldLength + " -> " + bytes.length);
                return true;
            } catch (ErrorResponseException | InternalException |
                    XmlParserException | InsufficientDataException |
                    InvalidKeyException | InvalidResponseException |
                    NoSuchAlgorithmException | ServerException |
                    IOException e) {
                e.printStackTrace();
                log.error("对象 [" + objectPrefix + objectPath + "] 追加异常");
            }
        }
        log.error("对象 [" + objectPrefix + objectPath + "] 不存在");
        return false;
    }

    /**
     * 对象复制
     *
     * @param sourceObjectPath 源对象路径名称
     * @param targetObjectPath 目标对象路径名称
     * @return 操作结果
     */
    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath) {
        return copyObject(sourceObjectPath, targetObjectPath, false);
    }

    /**
     * 对象复制
     *
     * @param sourceObjectPath 源对象路径名称
     * @param targetObjectPath 目标对象路径名称
     * @return 操作结果
     */
    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath, boolean isCover) {
        if (isCover) {
            return doCopyObject(objectPrefix + sourceObjectPath, objectPrefix + targetObjectPath);
        } else {
            if (isObjectExist(targetObjectPath)) {
                log.error("目标对象 [" + objectPrefix + targetObjectPath + "] 已存在");
                return false;
            } else {
                return doCopyObject(objectPrefix + sourceObjectPath, objectPrefix + targetObjectPath);
            }
        }
    }

    private boolean doCopyObject(String sourceObject, String targetObject) {
        MinioClient minio = getMinio();
        try {
            final CopySource copySource = CopySource.builder().bucket(bucket).object(sourceObject).build();
            final CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(targetObject)
                    .source(copySource)
                    .build();
            minio.copyObject(copyObjectArgs);
            log.debug("源对象 [" + sourceObject + "] -> " + "目标对象 [" + targetObject + "] 复制成功");
            return true;
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e1) {
            if (KEY_NOT_EXIST.equals(e1.getMessage())) {
                log.error("源对象 [" + sourceObject + "] 不存在");
            } else {
                log.error(e1.getMessage());
            }
        }
        return false;
    }

    /**
     * 对象获取
     *
     * @param objectPath 对象名称
     * @return 操作结果
     */
    @Override
    public CosObject getObject(String objectPath) {
        try {
            final GetObjectResponse object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            final CosObject cosObject = new CosObject();
            cosObject.setInputStream(object);
            cosObject.setPath(objectPrefix + objectPath);
            final String l = object.headers().get("Content-Length");
            final String c = object.headers().get("Content-Type");
            if (l == null) {
                log.error("对象 [" + objectPrefix + objectPath + "] 元数据缺少文件长度信息");
            }
            cosObject.setContentLength(l != null ? Long.parseLong(l) : null);
            cosObject.setContentType(c);
            log.debug("对象 [" + objectPrefix + objectPath + "] 获取成功");
            return cosObject;
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            log.error("对象 [" + objectPrefix + objectPath + "] 获取失败");
            return null;
        }
    }

    /**
     * 对象删除
     *
     * @param objectPath 对象名称
     * @return 操作结果
     */
    @Override
    public boolean deleteObject(String objectPath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(objectPrefix + objectPath).build());
            log.debug("对象 [" + objectPrefix + objectPath + "] 删除成功");
            return true;
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            e.printStackTrace();
            log.error("对象 [" + objectPrefix + objectPath + "] 删除失败");
            return false;
        }
    }

    /**
     * 对象是否存在
     *
     * @param objectPath 对象名称
     * @return
     */
    @Override
    public boolean isObjectExist(String objectPath) {
        try {
            getMinio().statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPrefix + objectPath)
                            .build());
            return true;
        } catch (ErrorResponseException | InternalException |
                XmlParserException | InsufficientDataException |
                InvalidKeyException | InvalidResponseException |
                NoSuchAlgorithmException | ServerException |
                IOException e) {
            if (FILE_NOT_EXIST.equals(e.getMessage())) {
                return false;
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void operateMinio(MinioOperator operator) {
        MinioClient minio = getMinio();
        operator.operator(minio);
    }

    /**
     * minio 操作接口
     */
    private interface MinioOperator {

        /**
         * 操作方法
         *
         * @param minio minioClient
         */
        void operator(MinioClient minio);
    }
}
