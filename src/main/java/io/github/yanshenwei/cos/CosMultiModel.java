package io.github.yanshenwei.cos;

import io.github.yanshenwei.cos.config.CosConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**********************************
 * @Author YSW
 * @Description 对象云存储 多云同时操作
 * @Date 2022/11/9 - 18:51
 **********************************/

@Component
public class CosMultiModel implements ObjectCloudStorage, ApplicationContextAware {

    @Resource
    private CosConstants.AliossConfig aliossConfig;

    @Resource
    private CosConstants.MinioConfig minioConfig;

    private MinioModel minioModel;

    private AliossModel aliossModel;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<String> beanList = Arrays.asList(applicationContext.getBeanDefinitionNames());
        //noinspection AlibabaUndefineMagicConstant
        if (beanList.contains("aliossModel")){
            aliossModel = applicationContext.getBean(AliossModel.class);
        }
        //noinspection AlibabaUndefineMagicConstant
        if (beanList.contains("minioModel")){
            minioModel = applicationContext.getBean(MinioModel.class);
        }
    }

    /**
     * 上传对象
     *
     * @param objectPath 对象存储路径
     * @param file       文件对象
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, File file) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.putObject(objectPath, file);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.putObject(objectPath, file);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.putObject(objectPath, file);
            final boolean result2 = minioModel.putObject(objectPath, file);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 上传流对象
     *
     * @param objectPath  对象存储路径
     * @param inputStream 字节数流对象
     * @param contentType 对象头类型
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, InputStream inputStream, String contentType) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.putObject(objectPath, inputStream, contentType);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.putObject(objectPath, inputStream, contentType);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.putObject(objectPath, inputStream, contentType);
            final boolean result2 = minioModel.putObject(objectPath, inputStream, contentType);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 上传字节数组对象
     *
     * @param objectPath 对象存储路径
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, byte[] content) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.putObject(objectPath, content);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.putObject(objectPath, content);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.putObject(objectPath, content);
            final boolean result2 = minioModel.putObject(objectPath, content);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 上传字节数组对象
     *
     * @param objectPath  对象存储路径
     * @param content     对象字节数组
     * @param contentType 对象头类型
     * @return 操作结果
     */
    @Override
    public boolean putObject(String objectPath, byte[] content, String contentType) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.putObject(objectPath, content, contentType);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.putObject(objectPath, content, contentType);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.putObject(objectPath, content, contentType);
            final boolean result2 = minioModel.putObject(objectPath, content, contentType);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 上传可追加字节流对象
     *
     * @param objectPath 对象存储路径
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean pudAppendableObject(String objectPath, byte[] content) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.pudAppendableObject(objectPath, content);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.pudAppendableObject(objectPath, content);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.pudAppendableObject(objectPath, content);
            final boolean result2 = minioModel.pudAppendableObject(objectPath, content);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 对象追加内容
     *
     * @param objectPath 对象存储路径
     * @param content    对象字节数组
     * @return 操作结果
     */
    @Override
    public boolean appendObject(String objectPath, byte[] content) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.appendObject(objectPath, content);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.appendObject(objectPath, content);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.appendObject(objectPath, content);
            final boolean result2 = minioModel.appendObject(objectPath, content);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 对象复制
     *
     * @param sourceObject 源对象路径名称
     * @param targetObject 目标对象路径名称
     * @return 操作结果
     */
    @Override
    public boolean copyObject(String sourceObject, String targetObject) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.copyObject(sourceObject, targetObject);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.copyObject(sourceObject, targetObject);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.copyObject(sourceObject, targetObject);
            final boolean result2 = minioModel.copyObject(sourceObject, targetObject);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 对象复制
     *
     * @param sourceObject 源对象路径名称
     * @param targetObject 目标对象路径名称
     * @param isCover      目标对象存在时是否覆盖
     * @return 操作结果
     */
    @Override
    public boolean copyObject(String sourceObject, String targetObject, boolean isCover) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.copyObject(sourceObject, targetObject, isCover);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.copyObject(sourceObject, targetObject, isCover);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.copyObject(sourceObject, targetObject, isCover);
            final boolean result2 = minioModel.copyObject(sourceObject, targetObject, isCover);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 对象获取
     *
     * @param objectPath 对象存储路径
     * @return 操作结果
     */
    @Override
    public CosObject getObject(String objectPath) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.getObject(objectPath);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.getObject(objectPath);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final CosObject aliossModelObject = aliossModel.getObject(objectPath);
            return aliossModelObject.getInputStream() != null ? aliossModelObject : minioModel.getObject(objectPath);
        }
        return null;
    }

    /**
     * 对象删除
     *
     * @param objectPath 对象存储路径
     * @return 操作结果
     */
    @Override
    public boolean deleteObject(String objectPath) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.deleteObject(objectPath);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.deleteObject(objectPath);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.deleteObject(objectPath);
            final boolean result2 = minioModel.deleteObject(objectPath);
            return result1 && result2;
        }
        return false;
    }

    /**
     * 对象是否存在
     *
     * @param objectPath 对象存储路径
     * @return
     */
    @Override
    public boolean isObjectExist(String objectPath) {
        if (aliossConfig.isEnable() && !minioConfig.isEnable()) {
            return aliossModel.isObjectExist(objectPath);
        }
        if (minioConfig.isEnable() && !aliossConfig.isEnable()) {
            return minioModel.isObjectExist(objectPath);
        }
        if (aliossConfig.isEnable() && minioConfig.isEnable()) {
            final boolean result1 = aliossModel.isObjectExist(objectPath);
            final boolean result2 = minioModel.isObjectExist(objectPath);
            return result1 && result2;
        }
        return false;
    }
}
