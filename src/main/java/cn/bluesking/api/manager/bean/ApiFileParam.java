package cn.bluesking.api.manager.bean;

/**
 * api封装上传文件参数
 * 
 * @author 随心
 *
 */
public class ApiFileParam implements Cloneable {

    /** 参数名 */
    private String fieldName;
    
    /** 注释 */
    private String comment;
    
    /** 是否必须 */
    private boolean necessary;

    public ApiFileParam(String fieldName, String comment, boolean necessary) {
        super();
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
