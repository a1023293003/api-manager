package cn.bluesking.api.manager.core.function;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.type.DoubleType;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.util.DoubleUtil;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 双精度浮点类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class DoubleTypeFunction {

    /**
     * 把传入字符串转换成双精度浮点类型
     * 
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DoubleType toDouble(String str) throws Exception {
        return (DoubleType) new DoubleType().toData(str);
    }
    
    /**
     * 在传入参数范围内随机返回一个双精度浮点数,闭合下限,开上限
     * 
     * @param a [DoubleType]下限
     * @param b [DoubleType]上限
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DoubleType randomDouble(DoubleType a, DoubleType b) throws Exception {
        Double min = (Double) a.getValue();
        Double max = (Double) b.getValue();
        return new DoubleType(RandomUtil.randomDouble(min, max));
    }
    
    /**
     * 在传入参数范围内随机返回一个双精度浮点数,闭合下限,开上限
     * 
     * @param a   [DoubleType]下限
     * @param b   [DoubleType]上限
     * @param num [IntegerType]小数位长度
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DoubleType randomDouble(DoubleType a, DoubleType b, IntegerType num) throws Exception {
        double min = (double) a.getValue();
        double max = (double) b.getValue();
        int number = (int) num.getValue();
        return new DoubleType(DoubleUtil.format(RandomUtil.randomDouble(min, max), number));
    }
    
}
