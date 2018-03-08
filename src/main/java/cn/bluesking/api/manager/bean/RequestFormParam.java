package cn.bluesking.api.manager.bean;

/**
 * 封装表单参数
 * 
 * @author 随心
 *
 */
public class RequestFormParam {

    /** 参数名 */
    private String fieldName;

    /** 参数值 */
    private Object fieldValue;

    public RequestFormParam(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

}
