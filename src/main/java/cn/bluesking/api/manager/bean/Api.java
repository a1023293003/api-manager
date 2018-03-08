package cn.bluesking.api.manager.bean;

import cn.bluesking.api.manager.constant.ContentTypeConstant;
import cn.bluesking.api.manager.constant.EncodingConstant;
import cn.bluesking.api.manager.util.CodecUtil;

/**
 * api信息对象
 * 
 * @author 随心
 *
 */
public class Api implements Cloneable {

    /** api唯一标识的id */
    private Integer aid;
    
    /** 项目唯一标识的id */
    private Integer pid;
    
    /** 请求方法 */
    private String requestMethod;
    
    /** 请求路径 */
    private String requestPath;
    
    /** api参数信息 */
    private ApiParam param;
    
    /** 编码类型,默认编码为UTF-8 */
    private String encoding = EncodingConstant.UTF_8;
    
    /** 请求响应内容类型,默认内容类型为JSON */
    private String contentType = ContentTypeConstant.JSON;
    
    /** 响应内容生成配置 */
    private String responseContentConfiguration;

    public Api() {};
    
    public Api(Integer aid, Integer pid, String requestMethod, String requestPath, ApiParam param, String encoding,
            String contentType, String responseContentConfiguration) {
        this.aid = aid;
        this.pid = pid;
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.param = param;
        this.encoding = encoding;
        this.contentType = contentType;
        encodeAndsetTemplate(responseContentConfiguration);
    }

    public Integer getAid() {
        return aid;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public ApiParam getParam() {
        return param;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }
    
    public void setParam(ApiParam param) {
        this.param = param;
    }

    public Integer getPid() {
        return pid;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseContentConfiguration(String responseContentConfiguration) {
        this.responseContentConfiguration = responseContentConfiguration;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getResponseContentConfiguration() {
        return responseContentConfiguration;
    }
    
    /**
     * 设置相应模板(未编码)
     * 
     * @param template [String]待编码模板
     */
    public void encodeAndsetTemplate(String template) {
        this.responseContentConfiguration = CodecUtil.encodeHTML(template);
    }
    
    /**
     * 获取响应数据模板(已解码)
     * 
     * @return [String]响应数据模板
     */
    public String decodeAndGetTemplate() {
        return CodecUtil.decodeHTML(responseContentConfiguration);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Api api = (Api) super.clone();
        if (param == null) {
            api.setParam(null);
        } else {
            api.setParam((ApiParam) param.clone());
        }
        return api;
    }
    
    public Api cloneApi() {
        try {
            return (Api) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Api [aid=" + aid + ", pid=" + pid + ", requestMethod=" + requestMethod + ", requestPath=" + requestPath
                + ", param=" + param + ", encoding=" + encoding + ", contentType=" + contentType
                + ", responseContentConfiguration=" + decodeAndGetTemplate() + "]";
    }
    
}
