package cn.bluesking.api.manager.core.function;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.type.DecimalType;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 高精度数据类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class DecimalTypeFunction {

    /**
     * 把传入字符串转换成decimal高精度数值类型
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DecimalType toDecimal(String str) throws Exception {
        return (DecimalType) new DecimalType().toData(str);
    }
    
    /**
     * 在传入参数范围内随机返回一个高精度数值对象,闭合下限,开上限
     * @param a   [DecimalType]下限
     * @param b   [DecimalType]上限
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DecimalType randomDecimal(DecimalType a, DecimalType b) {
        BigDecimal min = (BigDecimal) a.getValue();
        BigDecimal max = (BigDecimal) b.getValue();
        BigDecimal range = max.subtract(min);
        return new DecimalType(
                min.add(range.multiply(new BigDecimal(RandomUtil.randomDouble()))));
    }
    
    /**
     * 在传入参数范围内随机返回一个高精度数值对象,闭合下限,开上限
     * @param a   [DecimalType]下限
     * @param b   [DecimalType]上限
     * @param num [IntegerType]保留多少个小数位,默认四舍五入
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static DecimalType randomDecimal(DecimalType a, DecimalType b, IntegerType num) {
        int number = (int) num.getValue();
        if (number < 0) {
            throw new IllegalArgumentException("decimal高精度类型保留的小数位必须为非负数!num = " + number);
        } else {
            BigDecimal min = (BigDecimal) a.getValue();
            BigDecimal max = (BigDecimal) b.getValue();
            BigDecimal range = max.subtract(min);
            BigDecimal result = min.add(range.multiply(new BigDecimal(RandomUtil.randomDouble())));
            // 保留num位小数
            result = result.setScale(number, RoundingMode.HALF_UP);
            return new DecimalType(result);
        }
    }
}
