package cn.bluesking.api.manager.type;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;

/**
 * 日期类型
 * 
 * @author 随心
 *
 */
public class DateType implements BaseObjectType {

    /** 数据值 */
    private Date value;
    
    /** 日期格式 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public DateType() {};
    
    public DateType(Date value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = dateFormat.parse(str);
        return this;
    }

    @Override
    public String toString() {
        return dateFormat.format(value);
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
        entry.setKey("$date");
        entry.setValue(toString());
        return entry;
    }
    
}
