package cn.bluesking.api.manager.type;

import cn.bluesking.api.manager.util.StringUtil;

/**
 * int32整数类型
 * 
 * @author 随心
 *
 */
public class IntegerType implements BaseType {

    /** 数据值 */
    private int value;
    
    public IntegerType() {
        this(0);
    }
    
    public IntegerType(int value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = Integer.parseInt(StringUtil.trim(str));
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