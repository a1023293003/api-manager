package cn.bluesking.api.manager.bean;

/**
 * api封装表单参数
 * 
 * @author 随心
 *
 */
public class ApiFormParam implements Cloneable {

    /** 参数名 */
    private String fieldName;
    
    /** 注释 */
    private String comment;
    
    /** 是否必须 */
    private boolean necessary;
    
    public ApiFormParam(String fieldName, String comment, boolean necessary) {
        this.fieldName = fieldName;
        this.comment = comment;
        this.necessary = necessary;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getComment() {
        return comment;
    }

    public boolean isNecessary() {
        return necessary;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
