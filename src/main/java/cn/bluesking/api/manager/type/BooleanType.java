package cn.bluesking.api.manager.type;

/**
 * 布尔类型
 * 
 * @author 随心
 *
 */
public class BooleanType implements BaseType {

    /** 数据值 */
    private boolean value;
    
    public BooleanType() {};
    
    public BooleanType(boolean value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        if ("true".equals(str.trim())) {
            this.value = true;
        } else if ("false".equals(str.trim())) {
            this.value = false;
        } else {
            throw new IllegalArgumentException(
                    "传入字符串不合法!转换类型失败(String to BooleanType) str = " + str);
        }
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
}
