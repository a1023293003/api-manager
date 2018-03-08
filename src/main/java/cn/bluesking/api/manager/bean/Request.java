package cn.bluesking.api.manager.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 封装请求信息
 * 
 * @author 随心
 *
 */
public class Request {

    /** 请求方法 */
    private String requestMethod;

    /** 请求路径 */
    private String requestPath;

    /**
     * 构造方法
     * 
     * @param requestPath   [String]请求路径
     * @param requestMethod [String]请求方法
     */
    public Request(String requestPath, String requestMethod) {
        this.requestPath = requestPath;
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return "Request [requestMethod=" + requestMethod + ", requestPath=" + requestPath + "]";
    }
    
}
