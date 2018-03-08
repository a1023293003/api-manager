package cn.bluesking.api.manager.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.bluesking.api.manager.constant.ConfigConstant;
import cn.bluesking.api.manager.util.CaseUtil;
import cn.bluesking.api.manager.util.ConfigParser;
import cn.bluesking.api.manager.util.ConfigParser.Configuration;
import cn.bluesking.api.manager.util.PropsUtil;

/**
 * 属性文件助手类
 * 
 * @author 随心
 *
 */
public final class ConfigHelper {

    /** api配置文件 */
    private static final ConfigParser API_CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.API_CONFIG_FILE);

    /** 配置文件 */
    private static final ConfigParser CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);
    
    /**
     * 获取api配置文件中所有键值对
     * 
     * @return
     */
    public static Map<String, String> getApiConfigMap() {
        Map<String, Configuration> config = API_CONFIG_PROPS.getAllKeyValues();
        Map<String, String> keyValues = new HashMap<String, String>(config.size());
        Configuration configuration;
        for (Entry<String, Configuration> entry : config.entrySet()) {
            configuration = entry.getValue();
            keyValues.put(configuration.getKey(), configuration.getValue());
        }
        return keyValues;
    }
    
    /**
     * 添加或修改api内容到配置文件
     * 
     * @param key     [String]项目名称
     * @param value   [String]json格式的项目信息
     * @param comment [String]注释
     */
    public static void putApiConfigToProperties(String key, String value, String comment) {
        API_CONFIG_PROPS.putToProperties(key, value, comment);
    }
    
    /**
     * 添加或修改api内容但不更新到配置文件
     * 
     * @param key     [String]项目名称
     * @param value   [String]json格式的项目信息
     * @param comment [String]注释
     */
    public static void putApiConfig(String key, String value, String comment) {
        API_CONFIG_PROPS.put(key, value, comment);
    }
    
    /**
     * 删除指定api内容到配置文件
     * 
     * @param key [String]项目名称
     */
    public static void removeApiConfigToProperties(String key) {
        API_CONFIG_PROPS.removeToProperties(key);
    }
    
    /**
     * 删除指定api内容但不更新到配置文件
     * 
     * @param key [String]项目名称
     */
    public static void removeApiConfig(String key) {
        API_CONFIG_PROPS.remove(key);
    }
    
    /**
     * 更新指定api内容到配置文件
     * 
     * @param oldkey [String]旧键值
     * @param newKey [String]新键值
     * @param value  [String]值
     */
    public static void updateApiConfigToProperties(String oldKey, String newKey, String value) {
        if (API_CONFIG_PROPS.containsKey(oldKey)) {
            updateApiConfig(oldKey, newKey, value);
            updateApiConfig();
        } else {
            return;
        }
    }
    
    /**
     * 更新指定api内容但不更新到配置文件
     * 
     * @param oldkey [String]旧键值
     * @param newKey [String]新键值
     * @param value  [String]值
     */
    public static void updateApiConfig(String oldKey, String newKey, String value) {
        System.out.println("修改：" + oldKey + "," + newKey + "," + value);
        if (API_CONFIG_PROPS.containsKey(oldKey)) {
            System.out.println("包含");
            String comment = API_CONFIG_PROPS.getComment(oldKey);
            API_CONFIG_PROPS.remove(oldKey);
            API_CONFIG_PROPS.put(newKey, value, comment);
        } else {
            System.out.println("不包含");
            return;
        }
    }
    
    /**
     * 更新指定api内容但不更新到配置文件
     * 
     * @param key   [String]键值
     * @param value [String]值
     */
    public static void updateApiConfig(String key, String value) {
        if (API_CONFIG_PROPS.containsKey(key)) {
            API_CONFIG_PROPS.update(key, value);
        } else {
            return;
        }
    }
    
    /**
     * 更新api内容到配置文件
     */
    public static void updateApiConfig() {
        API_CONFIG_PROPS.updateToProperties();
    }
    
    /**
     * 获取上传文件的最大大小限制
     * 
     * @return
     */
    public static int getAppUploadLimit() {
        return CaseUtil.caseInt(CONFIG_PROPS.getValue(ConfigConstant.APP_UPLOAD_LIMIT), -1);
    }
    
    /**
     * 项目基础包名
     * 
     * @return
     */
    public static String getAppBasePackage() {
        return CONFIG_PROPS.getValue(ConfigConstant.APP_BASE_PACKAGE);
    }
    
    /**
     * 404响应错误提示
     * 
     * @return
     */
    public static String getNotFoundMessage() {
        return CONFIG_PROPS.getValue(ConfigConstant.NOT_FOUND_MESSAGE);
    }
    
    /**
     * 404响应编码
     * 
     * @return
     */
    public static String getNotFoundContentType() {
        return CONFIG_PROPS.getValue(ConfigConstant.NOT_FOUND_CONTENT_TYPE);
    }
    
    /**
     * 403响应错误提示
     * 
     * @return
     */
    public static String getForbiddenMessage() {
        return CONFIG_PROPS.getValue(ConfigConstant.FORBIDDEN_MESSAGE);
    }
    
    /**
     * 403响应编码
     * 
     * @return
     */
    public static String getForbiddenContentType() {
        return CONFIG_PROPS.getValue(ConfigConstant.FORBIDDEN_CONTENT_TYPE);
    }
    
    /**
     * 500响应错误提示
     * 
     * @return
     */
    public static String getServerErrorMessage() {
        return CONFIG_PROPS.getValue(ConfigConstant.SERVER_ERROR_MESSAGE);
    }
    
    /**
     * 500响应编码
     * 
     * @return
     */
    public static String getServerErrorContentType() {
        return CONFIG_PROPS.getValue(ConfigConstant.SERVER_ERROR_CONTENT_TYPE);
    }
    
}
