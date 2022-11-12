package io.github.yanshenwei.cos;

import java.io.InputStream;

/**********************************
 * @Author YSW
 * @Description
 * @Date 2022/11/10 - 09:43
 **********************************/

public class CosObject {

    private InputStream inputStream;

    private String path;

    private Long contentLength;

    private String contentType;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "CosObject{" +
                "inputStream=" + inputStream +
                ", contentLength=" + contentLength +
                ", contentType=" + contentType +
                '}';
    }
}
