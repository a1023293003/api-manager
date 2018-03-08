package cn.bluesking.api.manager.type;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;

/**
 * 时间戳类型
 * 
 * @author 随心
 *
 */
public class TimestampType implements BaseObjectType {

    /** 数据值, 数据类型必须为yyyy-mm-dd hh:mm:ss[.f...] 这样的格式, 中括号表示可选 */
    private Timestamp value;

    /** 日期格式 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public TimestampType() {}

    public TimestampType(Timestamp value) {
        this.value = value;
    }
    
    @Override
    public BaseType toData(String str) throws Exception {
        this.value = Timestamp.valueOf(str);
        return this;
    }
    
    @Override
    public String toString() {
        long microSecond = this.value.getNanos() / 1000;
        // 输出格式为YYYY-MM-DD hh:mm:ss.ffffff
        return this.dateFormat.format(this.value) + "." + 
                String.format("%06d", microSecond).substring(0, 6);
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
        entry.setKey("$timestamp");
        entry.setValue(toString());
        return entry;
    }
    
}
