package io.github.yanshenwei.cos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**********************************
 * @Author YSW
 * @Description 可配置变量
 * @Date 2020.12.17 - 14:48
 **********************************/

@Configuration
@ConfigurationProperties(prefix = "cos")
public class CosConstants {

    private String resourceHost;

    public String getResourceHost() {
        return resourceHost;
    }

    public void setResourceHost(String resourceHost) {
        this.resourceHost = resourceHost;
    }

    @Configuration
    @ConfigurationProperties(prefix = "cos.oss")
    public static class AliossConfig {

        private boolean enable;

        private String endpoint;

        private String accessKeyId;

        private String accessKeySecret;

        private String bucket;

        private String resourceHost;

        private String objectDirPrefix;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getResourceHost() {
            return resourceHost;
        }

        public void setResourceHost(String resourceHost) {
            this.resourceHost = resourceHost;
        }

        public String getObjectDirPrefix() {
            return objectDirPrefix;
        }

        public void setObjectDirPrefix(String objectDirPrefix) {
            this.objectDirPrefix = objectDirPrefix;
        }

        @Override
        public String toString() {
            return "AliossConfig{" +
                    "enable=" + enable +
                    ", endpoint='" + endpoint + '\'' +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + accessKeySecret + '\'' +
                    ", bucket='" + bucket + '\'' +
                    ", resourceHost='" + resourceHost + '\'' +
                    ", objectDirPrefix='" + objectDirPrefix + '\'' +
                    '}';
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "cos.minio")
    public static class MinioConfig {

        private boolean enable;

        private String endpoint;

        private String accessKeyId;

        private String accessKeySecret;

        private String bucket;

        private String resourceHost;

        private String objectDirPrefix;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getResourceHost() {
            return resourceHost;
        }

        public void setResourceHost(String resourceHost) {
            this.resourceHost = resourceHost;
        }

        public String getObjectDirPrefix() {
            return objectDirPrefix;
        }

        public void setObjectDirPrefix(String objectDirPrefix) {
            this.objectDirPrefix = objectDirPrefix;
        }

        @Override
        public String toString() {
            return "MinioConfig{" +
                    "enable=" + enable +
                    ", endpoint='" + endpoint + '\'' +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + accessKeySecret + '\'' +
                    ", bucket='" + bucket + '\'' +
                    ", resourceHost='" + resourceHost + '\'' +
                    ", objectDirPrefix='" + objectDirPrefix + '\'' +
                    '}';
        }
    }
}
