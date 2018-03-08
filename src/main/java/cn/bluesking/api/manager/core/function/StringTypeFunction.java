package cn.bluesking.api.manager.core.function;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.type.IntegerType;
import cn.bluesking.api.manager.util.RandomUtil;

/**
 * 字符串类型相关函数
 * 
 * @author 随心
 *
 */
@Function
public class StringTypeFunction {

    /**
     * 把传入参数转换成字符串
     * @param str [String]待转换参数
     * @return
     */
    @Function
    public static String toString(String str) {
        return str;
    }
    
    /**
     * 随机生成指定长度的字符串
     * @param length [IntegerType]生成字符串长度
     * @return
     * @throws Exception 转换类型失败
     */
    @Function
    public static String randomString(IntegerType length) throws Exception {
        int len = (int) length.getValue();
        return RandomUtil.randomString(len);
    }
    
    /**
     * 随机生成一个手机号码
     * @return
     */
    @Function
    public static String randomPhone() {
        return RandomUtil.randomPhone();
    }
    
    /**
     * 随机生成一个邮箱地址
     * @return
     */
    @Function
    public static String randomEmail() {
        return RandomUtil.randomEmail();
    }
    
    /**
     * 随机生成一个全局唯一标识
     * @return
     */
    @Function
    public static String randomUUID() {
        return RandomUtil.randomUUID();
    }
    
}
