package cn.bluesking.api.manager.core.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.annotation.NeedUniqueParameterAsKey;
import cn.bluesking.api.manager.type.DateType;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.util.DateUtil;
import cn.bluesking.api.manager.util.RandomUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * 日期类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class DateTypeFunction {

    /**
     * 把传入字符串转换成date日期类型
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DateType toDate(String str) throws Exception {
        return (DateType) new DateType().toData(str);
    }
    
    /**
     * 把传入字符串转换成date日期类型
     * @param str    [String]待转换字符串
     * @param format [String]日期字符串格式
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DateType toDate(String str, String format) throws Exception {
        if (StringUtil.isEmpty(str)) {
            throw new IllegalArgumentException("日期字符串不能为空!");
        } else if (StringUtil.isEmpty(format)) {
            throw new IllegalArgumentException("表示日期格式的字符串不能为空!");
        } else {
            DateFormat dateFormat = new SimpleDateFormat(format.trim());
            Date date = dateFormat.parse(str);
            return new DateType(date);
        }
    }
    
    /**
     * 返回当前日期
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DateType currentDate() throws Exception {
        return new DateType(new Date());
    }
    
    /**
     * 随机日期,在指定日期范围内随机生成一个日期
     * @param before [DateType]日期下限(包含)
     * @param after  [DateType]日期上限(不包含)
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DateType randomDate(DateType before, DateType after) throws Exception {
        Date beforeDate = (Date) before.getValue();
        Date afterDate = (Date) after.getValue();
        long min = beforeDate.getTime();
        long max = afterDate.getTime();
        return new DateType(new Date(RandomUtil.randomLong(min, max)));
    }
    
    /** 存储递增器的分组映射表 */
    private static final ConcurrentHashMap<String, Date> GROUP_MAP = 
            new ConcurrentHashMap<String, Date>();
    
    /**
     * 为Map中指定group对应的value递增并返回
     * @param year  [int]递增年数
     * @param month [int]递增月数
     * @param day   [int]递增天数
     * @param group [String]组别
     * @return
     */
    private static synchronized DateType addAndGet(int year, int month, int day, String group) {
        Date value = GROUP_MAP.get(group);
        value.setTime(value.getTime() + DateUtil.getAddDateTime(value, year, month, day));
        return new DateType(value);
    }
    
    /**
     * 递增的日期类型
     * @param start [DateType]起始日期
     * @param year  [IntegerType]每次递增的年数
     * @param month [IntegerType]每次递增的月数
     * @param day   [IntegerType]每次递增的天数
     * @param group [String]组别,用于区分每个迭代器的
     * @return
     */
    @Function
    @NeedUniqueParameterAsKey
    public static DateType increaseDate(DateType start, IntegerType year, 
            IntegerType month, IntegerType day, String group) {
        // 分组编号去除前后空格
        group = group.trim();
        Date startValue = (Date) start.getValue();
        int yearValue = (int) year.getValue();
        int monthValue = (int) month.getValue();
        int dayValue = (int) day.getValue();
        // Map中的value
        Date counter;
        if (!GROUP_MAP.containsKey(group)) {
            // 尝试插入键值对
            counter = GROUP_MAP.putIfAbsent(group, startValue);
            if (counter == null) {
                // 插入成功
                return start;
            } else {
                // 插入失败
                return addAndGet(yearValue, monthValue, dayValue, group);
            }
        } else {
            // 键值对已存在
            return addAndGet(yearValue, monthValue, dayValue, group);
        }
    }
    
}
