package io.github.yanshenwei.cos;

import io.github.yanshenwei.cos.config.CosConstants;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

/**********************************
 * @Author YSW
 * @Description minio v8.4.5
 * @Date 2020.12.24 - 15:09
 **********************************/

@Component
@ConditionalOnProperty(prefix = "cos.oss", name = "enable", havingValue = "true")
public class AliossModel implements ObjectCloudStorage {

    private static final Logger log = LoggerFactory.getLogger(AliossModel.class);

    @Resource
    private CosConstants.AliossConfig aliossConfig;

    private String bucket;

    private String objectPrefix;

    @PostConstruct
    private void init() {
        final String objectDirPrefix = aliossConfig.getObjectDirPrefix();
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
        this.bucket = aliossConfig.getBucket();
        log.debug("oss.object-dir-prefix: " + objectPrefix);
    }

    public OSS getOss() {
        return new OSSClientBuilder().build(
                aliossConfig.getEndpoint(),
                aliossConfig.getAccessKeyId(),
                aliossConfig.getAccessKeySecret()
        );
    }

    @Override
    public boolean putObject(String objectPath, File file) {
        OSS oss = getOss();
        if (!oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectPrefix + objectPath, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            log.debug("对象 [" + objectPrefix + objectPath + "] 上传成功");
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, InputStream inputStream, String contentType) {
        OSS oss = getOss();
        if (!oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectPrefix + objectPath, inputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, contentType == null ? "application/octet-stream" : contentType);
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        if (!oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectPrefix + objectPath, new ByteArrayInputStream(content));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, byte[] content, String contentType) {
        OSS oss = getOss();
        if (!oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectPrefix + objectPath, new ByteArrayInputStream(content));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, contentType);
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean pudAppendableObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        if (!oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, objectPrefix + objectPath, new ByteArrayInputStream(content));
            appendObjectRequest.setPosition(0L);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            appendObjectRequest.setMetadata(metadata);
            boolean isSuccessful = oss.appendObject(appendObjectRequest).getResponse().isSuccessful();
            oss.shutdown();
            if (!isSuccessful) {
                log.error("对象 [" + objectPrefix + objectPath + "] 上传失败");
            } else {
                log.debug("对象 [" + objectPrefix + objectPath + "] 上传成功");
            }
            return isSuccessful;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 已存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean appendObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        if (oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            HeadObjectRequest request = new HeadObjectRequest(bucket, objectPrefix + objectPath);
            String objectType = oss.headObject(request).getObjectType();
            //noinspection AlibabaUndefineMagicConstant
            if ("Appendable".equals(objectType)) {
                AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, objectPrefix + objectPath, new ByteArrayInputStream(content));
                long contentLength = oss.headObject(request).getContentLength();
                appendObjectRequest.setPosition(contentLength);
                appendObjectRequest.setInputStream(new ByteArrayInputStream(content));
                boolean isSuccessful = oss.appendObject(appendObjectRequest).getResponse().isSuccessful();
                oss.shutdown();
                if (!isSuccessful) {
                    log.error("对象 [" + objectPrefix + objectPath + "] 追加上传失败");
                }else {
                    log.debug("对象 [" + objectPrefix + objectPath + "] 追加上传成功 AppendLength  -> " + content.length);
                }
                return isSuccessful;
            } else {
                log.error("对象 [" + objectPrefix + objectPath + "] 类型错误 " + objectType + " ,追加失败");
            }
            oss.shutdown();
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 不存在");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath) {
        return copyObject(sourceObjectPath, targetObjectPath, false);
    }

    /**
     * 对象复制
     *
     * @param sourceObjectPath 源文件路径名称
     * @param targetObjectPath 目标文件路径名称
     * @param isCover
     * @return 操作结果
     */
    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath, boolean isCover) {
        OSS oss = getOss();
        if (Objects.isNull(targetObjectPath) || targetObjectPath.length() < 1) {
            log.error("目标对象名称错误");
            return false;
        }
        if (!oss.doesObjectExist(bucket, objectPrefix + sourceObjectPath)) {
            log.error("源对象 [" + objectPrefix + sourceObjectPath + "] 不存在");
            return false;
        }
        if (isCover) {
            oss.copyObject(bucket, objectPrefix + sourceObjectPath, bucket, objectPrefix + targetObjectPath);
            oss.shutdown();
            log.debug("源对象 ["+objectPrefix + sourceObjectPath+"] -> "+"目标对象 [" + objectPrefix + targetObjectPath + "] 复制成功");
            return true;
        } else {
            if (!oss.doesObjectExist(bucket, objectPrefix + targetObjectPath)) {
                oss.copyObject(bucket, objectPrefix + sourceObjectPath, bucket, objectPrefix + targetObjectPath);
                oss.shutdown();
                log.debug("源对象 ["+objectPrefix + sourceObjectPath+"] -> "+"目标对象 [" + objectPrefix + targetObjectPath + "] 复制成功");
                return true;
            } else {
                log.error("目标对象 [" + objectPrefix + targetObjectPath + "] 已存在");
                oss.shutdown();
                return false;
            }
        }
    }

    @Override
    public CosObject getObject(String objectPath) {
        //noinspection AlibabaUndefineMagicConstant
        if (objectPath.length() == 0 || objectPath.startsWith("/")) {
            return null;
        }
        OSS oss = getOss();
        if (oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            final OSSObject object = oss.getObject(bucket, objectPrefix + objectPath);
            final CosObject cosObject = new CosObject();
            cosObject.setInputStream(object.getObjectContent());
            cosObject.setPath(objectPrefix + objectPath);
            cosObject.setContentLength(object.getObjectMetadata().getContentLength());
            cosObject.setContentType(object.getObjectMetadata().getContentType());
            log.debug("对象 [" + objectPrefix + objectPath + "] 获取成功");
            return cosObject;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 不存在");
            oss.shutdown();
            return null;
        }
    }

    @Override
    public boolean deleteObject(String objectPath) {
        OSS oss = getOss();
        if (oss.doesObjectExist(bucket, objectPrefix + objectPath)) {
            oss.deleteObject(bucket, objectPrefix + objectPath);
            oss.shutdown();
            log.debug("对象 [" + objectPrefix + objectPath + "] 已删除");
            return true;
        } else {
            log.error("对象 [" + objectPrefix + objectPath + "] 不存在");
            oss.shutdown();
            return false;
        }
    }

    /**
     * 对象是否存在
     *
     * @param objectPath 文件名称
     * @return
     */
    @Override
    public boolean isObjectExist(String objectPath) {
        if (objectPath.length() == 0 || objectPath.startsWith("/")) {
            return false;
        }
        OSS oss = getOss();
        boolean exist = oss.doesObjectExist(bucket, objectPath);
        oss.shutdown();
        return exist;
    }

    private void operateOss(OssOperator operator) {
        OSS oss = getOss();
        operator.operator(oss);
        oss.shutdown();
    }

    /**
     * Oss 操作接口
     */
    private interface OssOperator {
        /**
         * 操作
         *
         * @param oss
         */
        void operator(OSS oss);
    }
}
