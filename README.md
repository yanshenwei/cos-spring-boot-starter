# cos-spring-boot-starter

对象云存储

## Latest Version: Maven Central 1.0.4
```
<dependency>
  <groupId>io.github.yanshenwei</groupId>
  <artifactId>cos-spring-boot-starter</artifactId>
  <version>1.0.4</version>
</dependency>
```

## use

```yaml
cos:
  # minio ${cos.minio.resource-host}/${cos.minio.object-dir-prefix}
  # oss ${cos.minio.resource-host}/${cos.minio.bucket}/${cos.minio.object-dir-prefix}
  resource-host: ${cos.minio.resource-host}/${cos.minio.bucket}/${cos.minio.object-dir-prefix}
  # MinIO配置
  minio:
    # 是否启动
    enable: true
    # 服务地址
    endpoint: https://xxx.xxx
    # 资源文件地址前缀
    resource-host: https://xxx.xxx
    # AccessKey ID
    access-key-id: "******"
    # AccessKey Secret
    access-key-secret: "******"
    # 存储桶名称
    bucket: uav-inspection
    object-dir-prefix: data
  # OSS配置
  oss:
    # 是否启动
    enable: false
    # 服务地址
    endpoint: "https://oss-cn-xxx.aliyuncs.com"
    # 资源文件地址前缀
    resource-host: https://xxx.xxx
    # AccessKey ID
    access-key-id: "******"
    # AccessKey Secret
    access-key-secret: "******"
    # 存储桶名称
    bucket: xxx
    object-dir-prefix: xxx
```