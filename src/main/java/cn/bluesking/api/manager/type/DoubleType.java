package cn.bluesking.api.manager.type;

/**
 * 双精度浮点类型
 * 
 * @author 随心
 *
 */
public class DoubleType implements BaseType {

    /** 数据值 */
    private double value;
    
    public DoubleType() {}
    
    public DoubleType(double value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = Double.parseDouble(str.trim());
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
