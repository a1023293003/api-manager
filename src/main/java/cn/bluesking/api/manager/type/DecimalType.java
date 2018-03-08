package cn.bluesking.api.manager.type;

import java.math.BigDecimal;

import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;

/**
 * 高精度数据类型
 * 
 * @author 随心
 *
 */
public class DecimalType implements BaseObjectType {

    /** 数据值 */
    private BigDecimal value;
    
    public DecimalType() {}
    
    public DecimalType(BigDecimal value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = new BigDecimal(str.trim());
        return this;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    /**
     * 获取对象的数据节点
     * @return
     */
    @Override
    public Entry toEntry() {
        Entry entry = new Entry();
        entry.setNext(null);
        entry.setKey("$decimal");
        entry.setValue(toString());
        return entry;
    }
    
}
