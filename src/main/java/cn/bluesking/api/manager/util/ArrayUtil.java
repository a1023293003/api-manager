package cn.bluesking.api.manager.util;

/**
 * 数组工具类
 * 
 * @author 随心
 *
 */
public final class ArrayUtil {

    /**
     * 判断数组是否为空
     * 
     * @param objs [Object[]]待判断数组
     * @return [boolean]如果传入数组为null或数组长度为0则该方法返回true,否则该方法返回false
     */
    public static boolean isEmpty(Object[] objs) {
        return objs == null || objs.length <= 0;
    }
    
    /**
     * 判断数组是否非空
     * @param objs [Object[]]待判断数组
     * @return [boolean]如果传入数组不为null且数组长度大于0则该方法返回true,否则该方法返回false
     */
    public static boolean isNotEmpty(Object[] objs) {
        return !isEmpty(objs);
    }
}
