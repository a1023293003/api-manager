package cn.bluesking.api.manager.constant;

/**
 * 提供相关配置项常量
 * 
 * @author 随心
 *
 */
public interface ConfigConstant {

    /** api配置文件 */
    String API_CONFIG_FILE = "api-config.properties";
    
    /** 配置文件 */
    String CONFIG_FILE = "config.properties";
    
    /** 上传文件的最大大小限制 */
    String APP_UPLOAD_LIMIT = "bluesking.api.manager.app.upload_limit";
    
    /** 项目基础包名 */
    String APP_BASE_PACKAGE = "bluesking.api.manager.app.base_package";
    
    /** 404响应错误提示 */
    String NOT_FOUND_MESSAGE = "bluesking.api.manager.app.not_found_message";
    
    /** 404响应编码 */
    String NOT_FOUND_CONTENT_TYPE = "bluesking.api.manager.app.not_found_content_type";
    
    /** 403响应错误提示 */
    String FORBIDDEN_MESSAGE = "bluesking.api.manager.app.forbidden_message";
    
    /** 403响应编码 */
    String FORBIDDEN_CONTENT_TYPE = "bluesking.api.manager.app.forbidden_content_type";
    
    /** 500响应错误提示 */
    String SERVER_ERROR_MESSAGE = "bluesking.api.manager.app.server_error_message";
    
    /** 500响应编码 */
    String SERVER_ERROR_CONTENT_TYPE = "bluesking.api.manager.app.server_error_content_type";
    
}
