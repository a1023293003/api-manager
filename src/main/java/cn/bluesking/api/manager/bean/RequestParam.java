package cn.bluesking.api.manager.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bluesking.api.manager.util.CaseUtil;
import cn.bluesking.api.manager.util.CollectionUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * 请求参数对象
 * 
 * @author 随心
 *
 */
public class RequestParam {
    
    /** 封装表单参数集合 */
    private List<RequestFormParam> formParamList;
    
    /** 封装上传文件参数集合 */
    private List<RequestFileParam> fileParamList;
    
    /** 请求参数映射 */
    private Map<String, Object> fieldMap;
    
    /** 上传文件映射 */
    private Map<String, List<RequestFileParam>> fileMap;
    
    public RequestParam(List<RequestFormParam> formParamList) {
        this.formParamList = formParamList;
        // 获取请求参数映射
        this.fieldMap = getInitFieldMap();
    }
    
    public RequestParam(List<RequestFormParam> formParamList, List<RequestFileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
        // 获取请求参数映射
        this.fieldMap = getInitFieldMap();
        // 获取上传文件映射
        this.fileMap = getInitFileMap();
    }

    /**
     * 获取请求参数映射
     * 
     * @return
     */
    private Map<String,Object> getInitFieldMap() {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        if(CollectionUtil.isNotEmpty(formParamList)) {
            // 遍历所有请求参数，存到Map容器中
            for(RequestFormParam formParam : formParamList) {
                String fieldName = formParam.getFieldName();
                Object fieldValue = formParam.getFieldValue();
                if(fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
        return fieldMap;
    }
    
    /**
     * 获取上传文件映射
     * 
     * @return
     */
    private Map<String, List<RequestFileParam>> getInitFileMap() {
        Map<String, List<RequestFileParam>> fileMap = new HashMap<String, List<RequestFileParam>>();
        if(CollectionUtil.isNotEmpty(fileParamList)) {
            // 遍历所有上传文件，存到Map容器中
            for(RequestFileParam fileParam : fileParamList) {
                String fieldName = fileParam.getFieldName();
                List<RequestFileParam> fileParamList;
                if(fileMap.containsKey(fieldName)) {
                    fileParamList = fileMap.get(fieldName);
                } else {
                    fileParamList = new ArrayList<RequestFileParam>();
                }
                fileParamList.add(fileParam);
                fileMap.put(fieldName, fileParamList);
            }
        }
        return fileMap;
    }

    /**
     * 获取上传文件映射
     * 
     * @return
     */
    public Map<String, List<RequestFileParam>> getFileMap() {
        return this.fileMap;
    }
    
    /**
     * 获取请求参数映射
     * 
     * @return
     */
    public Map<String, Object> getFieldMap() {
        return this.fieldMap;
    }
    
    /**
     * 获取所有上传文件
     * 
     * @param fieldName [String]表单参数名
     * @return
     */
    public List<RequestFileParam> getFileList(String fieldName) {
        return this.fileMap.get(fieldName);
    }
    
    /**
     * 获取唯一上传文件
     * 
     * @param fieldName [String]表单参数名
     * @return
     */
    public RequestFileParam getFile(String fieldName) {
        List<RequestFileParam> fileParamList = getFileList(fieldName);
        if(CollectionUtil.isNotEmpty(fileParamList) && fileParamList.size() == 1) {
            return fileParamList.get(0);
        }
        return null;
    }
    
    /**
     * 判断请求参数是否为空
     * 
     * @return
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(formParamList) &&
                CollectionUtil.isEmpty(fileParamList);
    }
    
    /**
     * 判断是否存在指定参数名的参数
     * 
     * @param fieldName [String]参数名
     * @return [boolean]如果存在指定参数名的请求参数此方法返回true,否则方法返回false
     */
    public boolean containsField(String fieldName) {
        if (CollectionUtil.isNotEmpty(fieldMap)) {
            return fieldMap.containsKey(fieldName);
        } else if (CollectionUtil.isNotEmpty(fileMap)) {
            return fileMap.containsKey(fieldName);
        } else {
            return false;
        }
    }
    
    /**
     * 根据参数名获取long类型参数值
     * 
     * @param name [String]参数名
     * @return
     */
    public long getLong(String name) {
        return CaseUtil.caseLong(fieldMap.get(name));
    }
    
    /**
     * 根据参数名获取double类型数值
     * 
     * @param name [String]参数名
     * @return
     */
    public double getDouble(String name) {
        return CaseUtil.caseDouble(fieldMap.get(name));
    }
    
    /**
     * 根据参数名获取int类型数值
     * 
     * @param name [String]参数名
     * @return
     */
    public int getInt(String name) {
        return CaseUtil.caseInt(fieldMap.get(name));
    }
    
    /**
     * 根据参数名获取String类型数值
     * 
     * @param name [String]参数名
     * @return
     */
    public String getString(String name) {
        return CaseUtil.caseString(fieldMap.get(name));
    }
    
    /**
     * 根据参数名获取boolean类型数值
     * 
     * @param name [String]参数名
     * @return
     */
    public boolean getBoolean(String name) {
        return CaseUtil.caseBoolean(fieldMap.get(name));
    }
    
}
