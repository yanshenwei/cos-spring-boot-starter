package io.github.yanshenwei.cos;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import io.github.yanshenwei.cos.config.CosConstants;
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

    private String endpoint;

    private String resourceHost;

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
        log.debug("oss.object-dir-prefix: " + objectPrefix);
        bucket = aliossConfig.getBucket();
        resourceHost = aliossConfig.getResourceHost();
        endpoint = aliossConfig.getEndpoint();
        if (!isBucketExists(bucket)) {
            createBucket(bucket);
        }
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
        String path = getFormatObjectPath(objectPath);
        if (!oss.doesObjectExist(bucket, path)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            log.debug("?????? [" + path + "] ????????????");
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, InputStream inputStream, String contentType) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (!oss.doesObjectExist(bucket, path)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, inputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, contentType == null ? "application/octet-stream" : contentType);
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (!oss.doesObjectExist(bucket, path)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, new ByteArrayInputStream(content));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean putObject(String objectPath, byte[] content, String contentType) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (!oss.doesObjectExist(bucket, path)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, new ByteArrayInputStream(content));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, contentType);
            putObjectRequest.setMetadata(metadata);
            oss.putObject(putObjectRequest);
            oss.shutdown();
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean pudAppendableObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (!oss.doesObjectExist(bucket, path)) {
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, path, new ByteArrayInputStream(content));
            appendObjectRequest.setPosition(0L);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.CONTENT_TYPE, "application/octet-stream");
            appendObjectRequest.setMetadata(metadata);
            boolean isSuccessful = oss.appendObject(appendObjectRequest).getResponse().isSuccessful();
            oss.shutdown();
            if (!isSuccessful) {
                log.error("?????? [" + path + "] ????????????");
            } else {
                log.debug("?????? [" + path + "] ????????????");
            }
            return isSuccessful;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean appendObject(String objectPath, byte[] content) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (oss.doesObjectExist(bucket, path)) {
            HeadObjectRequest request = new HeadObjectRequest(bucket, path);
            String objectType = oss.headObject(request).getObjectType();
            //noinspection AlibabaUndefineMagicConstant
            if ("Appendable".equals(objectType)) {
                AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, path, new ByteArrayInputStream(content));
                long contentLength = oss.headObject(request).getContentLength();
                appendObjectRequest.setPosition(contentLength);
                appendObjectRequest.setInputStream(new ByteArrayInputStream(content));
                boolean isSuccessful = oss.appendObject(appendObjectRequest).getResponse().isSuccessful();
                oss.shutdown();
                if (!isSuccessful) {
                    log.error("?????? [" + path + "] ??????????????????");
                } else {
                    log.debug("?????? [" + path + "] ?????????????????? AppendLength  -> " + content.length);
                }
                return isSuccessful;
            } else {
                log.error("?????? [" + path + "] ???????????? " + objectType + " ,????????????");
            }
            oss.shutdown();
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath) {
        return copyObject(sourceObjectPath, targetObjectPath, false);
    }

    /**
     * ????????????
     *
     * @param sourceObjectPath ?????????????????????
     * @param targetObjectPath ????????????????????????
     * @param isCover
     * @return ????????????
     */
    @Override
    public boolean copyObject(String sourceObjectPath, String targetObjectPath, boolean isCover) {
        OSS oss = getOss();
        if (Objects.isNull(targetObjectPath) || targetObjectPath.length() < 1) {
            log.error("????????????????????????");
            return false;
        }
        String sourcePath = getFormatObjectPath(sourceObjectPath);
        String targetPath = getFormatObjectPath(targetObjectPath);
        if (!oss.doesObjectExist(bucket, sourcePath)) {
            log.error("????????? [" + sourcePath + "] ?????????");
            return false;
        }
        if (isCover) {
            oss.copyObject(bucket, sourcePath, bucket, targetPath);
            oss.shutdown();
            log.debug("????????? [" + sourcePath + "] -> " + "???????????? [" + targetPath + "] ????????????");
            return true;
        } else {
            if (!oss.doesObjectExist(bucket, targetPath)) {
                oss.copyObject(bucket, sourcePath, bucket, targetPath);
                oss.shutdown();
                log.debug("????????? [" + sourcePath + "] -> " + "???????????? [" + targetPath + "] ????????????");
                return true;
            } else {
                log.error("???????????? [" + targetPath + "] ?????????");
                oss.shutdown();
                return false;
            }
        }
    }

    @Override
    public CosObject getObject(String objectPath) {
        //noinspection AlibabaUndefineMagicConstant
        if (objectPath.length() == 0) {
            return null;
        }
        if (objectPath.startsWith("/")){
            objectPath = objectPath.substring(1);
        }
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (path.startsWith("/")){
            path = path.substring(1);
        }
        if (oss.doesObjectExist(bucket, path)) {
            if (path.startsWith("/")){
                path = path.substring(1);
            }
            final OSSObject object = oss.getObject(bucket, path);
            final CosObject cosObject = new CosObject();
            cosObject.setInputStream(object.getObjectContent());
            cosObject.setPath(path);
            cosObject.setContentLength(object.getObjectMetadata().getContentLength());
            cosObject.setContentType(object.getObjectMetadata().getContentType());
            log.debug("?????? [" + path + "] ????????????");
            return cosObject;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return null;
        }
    }


    /**
     * ????????????(????????????????????????)
     * @param objectUrl ??????????????????
     * @return ????????????
     */
    @Override
    public CosObject getUrlObject(String objectUrl) {
        if (resourceHost == null){
            log.error("?????????????????????");
            return null;
        }
        if (objectUrl.length() == 0){
            return null;
        }
        if (objectUrl.startsWith(aliossConfig.getResourceHost())){
            final String objectPath = objectUrl.substring(
                    resourceHost.length() +
                            objectPrefix.length() +
                            (objectPrefix.length() > 0 ? 1 : 2));
            return getObject(objectPath);
        }
        return null;
    }

    @Override
    public boolean deleteObject(String objectPath) {
        OSS oss = getOss();
        String path = getFormatObjectPath(objectPath);
        if (oss.doesObjectExist(bucket, path)) {
            oss.deleteObject(bucket, path);
            oss.shutdown();
            log.debug("?????? [" + path + "] ?????????");
            return true;
        } else {
            log.error("?????? [" + path + "] ?????????");
            oss.shutdown();
            return false;
        }
    }

    /**
     * ??????????????????
     *
     * @param objectPath ????????????
     * @return
     */
    @Override
    public boolean isObjectExist(String objectPath) {
        if (objectPath.length() == 0 || objectPath.startsWith("/")) {
            return false;
        }
        OSS oss = getOss();
        boolean exist = oss.doesObjectExist(bucket, getFormatObjectPath(objectPath));
        oss.shutdown();
        return exist;
    }

    /**
     * ???????????? url
     * @param objectPath ????????????
     * @return
     */
    @Override
    public String getObjectUrl(String objectPath) {
        if (objectPath == null || objectPath.trim().length() == 0) {
            return null;
        }
        final String formatObjectPath = getFormatObjectPath(objectPath);
        return aliossConfig.getResourceHost() + ("/"+ formatObjectPath).replaceAll("/+", "/");
    }

    /**
     * ??????????????????
     *
     * @param url ??????????????????
     * @return
     */
    @Override
    public String objectUrlToPath(String url) {
        if (url == null || url.trim().length() < (aliossConfig.getResourceHost().length() + 1)) {
            return url;
        }
        return url.replace(aliossConfig.getResourceHost(), "");
    }

    @Override
    public boolean isBucketExists(String bucketName) {
        OSS oss = getOss();
        final GenericRequest genericRequest = new GenericRequest();
        genericRequest.setEndpoint(endpoint);
        boolean exist = oss.doesBucketExist(bucketName);
        if (exist){
            log.debug("bucket ["+bucketName+"] ?????????");
        }
        oss.shutdown();
        return exist;
    }

    @Override
    public boolean createBucket(String bucketName) {
        OSS oss = getOss();
        final CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setEndpoint(endpoint);
        createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
        oss.createBucket(createBucketRequest);
        oss.shutdown();
        return true;
    }

    private String getFormatObjectPath(String objectPath){
        String path = (objectPrefix + objectPath).replaceAll("/+", "/");
        if (path.startsWith("/")){
            path = path.substring(1);
        }
        return path;
    }

    private void operateOss(OssOperator operator) {
        OSS oss = getOss();
        operator.operator(oss);
        oss.shutdown();
    }

    /**
     * Oss ????????????
     */
    private interface OssOperator {
        /**
         * ??????
         *
         * @param oss
         */
        void operator(OSS oss);
    }
}
