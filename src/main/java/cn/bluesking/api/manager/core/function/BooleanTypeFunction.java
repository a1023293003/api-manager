package cn.bluesking.api.manager.core.function;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.type.BooleanType;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 布尔类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class BooleanTypeFunction {

    /**
     * 把传入字符串转换成boolean布尔类型
     * @param str [String]待转换字符串
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static BooleanType toBoolean(String str) throws Exception {
        return (BooleanType) new BooleanType().toData(str);
    }
    
    /**
     * 随机生成一个布尔值
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static BooleanType randomBoolean() throws Exception {
        return new BooleanType(RandomUtil.randomBoolean());
    }
}
