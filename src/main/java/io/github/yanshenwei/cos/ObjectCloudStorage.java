package io.github.yanshenwei.cos;

import java.io.File;
import java.io.InputStream;

/**********************************
 * @Author YSW
 * @Description 对象云存储
 * @Date 2022/10/28 - 16:39
 **********************************/

public interface ObjectCloudStorage {

    /**
     * 上传对象
     * @param objectPath 对象存储路径
     * @param file 文件对象
     * @return 操作结果
     */
    default boolean putObject(String objectPath, File file) {

        return false;
    }

    /**
     * 上传流对象
     * @param objectPath 对象存储路径
     * @param inputStream 字节数流对象
     * @param contentType 对象头类型
     * @return 操作结果
     */
    default boolean putObject(String objectPath, InputStream inputStream, String contentType) {
        return false;
    }

    /**
     * 上传字节数组对象
     * @param objectPath 对象存储路径
     * @param content 对象字节数组
     * @return 操作结果
     */
    default boolean putObject(String objectPath, byte[] content) {
        return false;
    }

    /**
     * 上传字节数组对象
     * @param objectPath 对象存储路径
     * @param content 对象字节数组
     * @param contentType 对象头类型
     * @return 操作结果
     */
    default boolean putObject(String objectPath, byte[] content, String contentType) {
        return false;
    }

    /**
     * 上传可追加字节流对象
     * @param objectPath 对象存储路径
     * @param content 对象字节数组
     * @return 操作结果
     */
    default boolean pudAppendableObject(String objectPath, byte[] content) {
        return false;
    }

    /**
     * 对象追加内容
     * @param objectPath 对象存储路径
     * @param content 对象字节数组
     * @return 操作结果
     */
    default boolean appendObject(String objectPath, byte[] content) {
        return false;
    }

    /**
     * 对象复制
     * @param sourceObject 源对象路径名称
     * @param targetObject 目标对象路径名称
     * @return 操作结果
     */
    default boolean copyObject(String sourceObject, String targetObject) {
        return false;
    }

    /**
     * 对象复制
     * @param sourceObject 源对象路径名称
     * @param targetObject 目标对象路径名称
     * @param isCover 目标对象存在时是否覆盖
     * @return 操作结果
     */
    default boolean copyObject(String sourceObject, String targetObject, boolean isCover) {
        return false;
    }

    /**
     * 对象获取
     * @param objectPath 对象存储路径
     * @return 操作结果
     */
    default CosObject getObject(String objectPath) {
        return null;
    }

    /**
     * 对象删除
     * @param objectPath 对象存储路径
     * @return 操作结果
     */
    default boolean deleteObject(String objectPath) {
        return false;
    }

    /**
     * 对象是否存在
     *
     * @param objectPath 对象存储路径
     * @return
     */
    default boolean isObjectExist(String objectPath) {
        return false;
    }
}
