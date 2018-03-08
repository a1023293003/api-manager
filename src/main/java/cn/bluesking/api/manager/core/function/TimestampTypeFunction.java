package cn.bluesking.api.manager.core.function;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.annotation.NeedUniqueParameterAsKey;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.type.TimestampType;
import cn.bluesking.api.manager.util.DateUtil;
import cn.bluesking.api.manager.util.RandomUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * 时间戳类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class TimestampTypeFunction {

    /**
     * 返回当前时间戳
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static TimestampType currentTimestamp() throws Exception {
        return new TimestampType(new Timestamp(System.currentTimeMillis()));
    }
    
    /**
     * 把传入字符串转换成timestamp时间戳类型
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static TimestampType toTimestamp(String str) throws Exception {
        if (StringUtil.isEmpty(str)) {
            throw new IllegalArgumentException("待转换时间戳字符串不能为空!");
        } else {
            return (TimestampType) new TimestampType().toData(str);
        }
    }
    
    /**
     * 随机时间戳,在指定日期范围内随机生成一个时间戳
     * @param before [TimestampType]日期下限(包含)
     * @param after  [TimestampType]日期上限(不包含)
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static TimestampType randomTimestamp(TimestampType before, TimestampType after) 
            throws Exception {
        Timestamp beforeTime = (Timestamp) before.getValue();
        Timestamp afterTime = (Timestamp) after.getValue();
        long min = beforeTime.getTime();
        long max = afterTime.getTime();
        return new TimestampType(new Timestamp(RandomUtil.randomLong(min, max)));
    }
    
    /** 存储递增器的分组映射表 */
    private static final ConcurrentHashMap<String, Timestamp> GROUP_MAP = 
            new ConcurrentHashMap<String, Timestamp>();
    
    /**
     * 为Map中指定group对应的value递增并返回
     * @param year        [int]递增年数
     * @param month       [int]递增月数
     * @param day         [int]递增天数
     * @param hour        [int]递增小时数
     * @param minute      [int]递增分钟数
     * @param second      [int]递增秒数
     * @param microSecond [int]递增微秒数
     * @param group       [String]组别
     * @return
     */
    private static synchronized TimestampType addAndGet(int year, int month, int day, 
            int hour, int minute, int second, int microSecond, String group) {
        Timestamp value = GROUP_MAP.get(group);
        int nanos = value.getNanos() + microSecond * 1000;
        value.setTime(value.getTime() + 
                DateUtil.getAddDateTime(value, year, month, day, hour, minute, second) + 
                nanos / 1000000);
        value.setNanos(nanos % 1000000000);
        return new TimestampType(value);
    }
    
    /**
     * 递增的日期类型
     * @param start       [DateType]起始日期
     * @param year        [IntegerType]每次递增的年数
     * @param month       [IntegerType]每次递增的月数
     * @param day         [IntegerType]每次递增的天数
     * @param hour        [IntegerType]递增小时数
     * @param minute      [IntegerType]递增分钟数
     * @param second      [IntegerType]递增秒数
     * @param microSecond [IntegerType]递增微秒数
     * @param group       [String]组别,用于区分每个迭代器的
     * @return
     */
    @Function
    @NeedUniqueParameterAsKey
    public static TimestampType increaseTimestamp(TimestampType start, IntegerType year, 
            IntegerType month, IntegerType day, IntegerType hour, IntegerType minute,
            IntegerType second, IntegerType microSecond, String group) {
        // 分组编号去除前后空格
        group = group.trim();
        Timestamp startValue = (Timestamp) start.getValue();
        int yearValue = (int) year.getValue();
        int monthValue = (int) month.getValue();
        int dayValue = (int) day.getValue();
        int hourValue = (int) hour.getValue();
        int minuteValue = (int) minute.getValue();
        int secondValue = (int) second.getValue();
        int microSecondValue = (int) microSecond.getValue();
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
                return addAndGet(yearValue, monthValue, dayValue, 
                        hourValue, minuteValue, secondValue, microSecondValue, group);
            }
        } else {
            // 键值对已存在
            return addAndGet(yearValue, monthValue, dayValue, 
                    hourValue, minuteValue, secondValue, microSecondValue, group);
        }
    }
    
}
