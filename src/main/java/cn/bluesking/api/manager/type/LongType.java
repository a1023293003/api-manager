package cn.bluesking.api.manager.type;

import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;

/**
 * int64位整数类型
 * 
 * @author 随心
 *
 */
public class LongType implements BaseObjectType {

    /** 数据值 */
    private long value;
    
    public LongType() {}
    
    public LongType(long value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = Long.parseLong(str.trim());
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
    
    /**
     * 获取对象的数据节点
     * @return
     */
    @Override
    public Entry toEntry() {
        Entry entry = new Entry();
        entry.setNext(null);
        entry.setKey("$numberLong");
        entry.setValue(toString());
        return entry;
    }
    
}
