package cn.bluesking.api.manager.core.function;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.type.LongType;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 64位整数类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class LongTypeFunction {

    /**
     * 把传入字符串转换成int64位整数
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static LongType toLong(String str) throws Exception {
        return (LongType) new LongType().toData(str);
    }
    
    /**
     * 在传入参数范围内随机返回一个双精度浮点数,闭合下限,开上限
     * @param a [DoubleType]下限
     * @param b [DoubleType]上限
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static LongType randomLong(LongType a, LongType b) throws Exception {
        long min = (long) a.getValue();
        long max = (long) b.getValue();
        return new LongType(RandomUtil.randomLong(min, max));
    }
    
}
