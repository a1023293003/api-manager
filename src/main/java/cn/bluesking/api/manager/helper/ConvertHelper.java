package cn.bluesking.api.manager.helper;

import cn.bluesking.api.manager.type.BaseType;

/**
 * 数据类型转换助手类
 * 
 * @author 随心
 *
 */
public final class ConvertHelper {

    /**
     * 把原参数对象转换成指定Class类型的参数对象
     * <pre>
     * 无论是原参数对象的类型还是目标参数对应的Class类型,
     * 都必须是String字符串或者BaseType的子类,否则抛出异常.
     * </pre>
     * 
     * @param cls [Class<?>]方法参数对应的Class
     * @param obj [Object]原参数对象
     * @return
     */
    public static Object convertParameter(Class<?> cls, Object obj) throws IllegalArgumentException {
        if (cls == null || obj == null) {
            ExceptionHelper.printAndExit("参数类型转换方法传入参数不能为空", null);
        } else if (cls == String.class) {
            // 需求参数类型为字符串
            if (obj.getClass() == String.class) {                             // String
                return obj;
            } else if (BaseType.class.isAssignableFrom(obj.getClass())) {     // BaseType to String
                return ((BaseType) obj).toString();
            } else {
                throw new IllegalArgumentException(obj.getClass().getName() + 
                        "原参数类型不合法!cls = " + cls.getName());
            }
        } else if (BaseType.class.isAssignableFrom(cls)) {
            // 需求参数类型为BaseType子类
            if (obj.getClass() == String.class) {                             // String to BaseType
                try {
                    return ((BaseType) cls.newInstance()).toData((String) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ClassCastException(
                            "参数对象类型转换异常[" + obj + "]String to " + cls.getName());
                }
            } else if (BaseType.class.isAssignableFrom(obj.getClass())) {     // BaseType to BaseType
                try {
                    return ((BaseType) cls.newInstance()).toData(obj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ClassCastException("参数对象类型转换异常" + 
                            obj.getClass().getName() + " to " + cls.getName());
                }
            } else {
                throw new IllegalArgumentException(obj.getClass().getName() + 
                        "原参数类型不合法!cls = " + cls.getName());
            }
        } else {
            throw new IllegalArgumentException(cls.getName() + "需求参数类型不合法!");
        }
        return obj;
    }
}
