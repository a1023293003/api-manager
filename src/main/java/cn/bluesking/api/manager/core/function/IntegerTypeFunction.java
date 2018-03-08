package cn.bluesking.api.manager.core.function;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.annotation.NeedUniqueParameterAsKey;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 32位整数类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class IntegerTypeFunction {

    /**
     * 把传入字符串转换成int32位整数
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static IntegerType toInteger(String str) throws Exception {
        return (IntegerType) new IntegerType().toData(str);
    }
    
    /**
     * 在传入参数范围内随机返回一个int32的整数,闭合下限,开上限
     * @param a [IntegerType]下限
     * @param b [IntegerType]上限
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static IntegerType randomInt(IntegerType a, IntegerType b) throws Exception {
        int min = (int) a.getValue();
        int max = (int) b.getValue();
        return new IntegerType(RandomUtil.randomInt(min, max));
    }
    
    /** 存储递增器的分组映射表 */
    private static final ConcurrentHashMap<String, AtomicInteger> GROUP_MAP = 
            new ConcurrentHashMap<String, AtomicInteger>();
    
    /**
     * 为Map中指定group对应的value递增并返回
     * @param step  [int]递增数值
     * @param group [String]组别,key
     * @return
     */
    private static IntegerType addAndGet(int step, String group) {
        AtomicInteger counter = GROUP_MAP.get(group);
        return new IntegerType(counter.addAndGet(step));
    }
    
    /**
     * 递增的int32位整数
     * @param start [IntegerType]起始值
     * @param step  [IntegerType]每次递增的值
     * @param group [String]组别,用于区分每个迭代器的
     * @return
     */
    @Function
    @NeedUniqueParameterAsKey
    public static IntegerType increaseInt(IntegerType start, IntegerType step, String group) {
        group = group.trim();
        int startValue = (int) start.getValue();
        int stepValue = (int) step.getValue();
        if (!GROUP_MAP.containsKey(group)) {
            // 尝试插入新的键值对
            AtomicInteger counter = GROUP_MAP.putIfAbsent(group, new AtomicInteger(startValue));
            if (counter == null) {
                // 插入成功
                return start;
            } else {
                // 插入失败
                return addAndGet(stepValue, group);
            }
        } else {
            // 存在键值对
            return addAndGet(stepValue, group);
        }
    }
    
}
