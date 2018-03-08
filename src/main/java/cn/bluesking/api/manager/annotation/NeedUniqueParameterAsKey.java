package cn.bluesking.api.manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个方法,表示该方法需要一个唯一的传入参数作为键值对的的键值
 * 
 * @author 随心
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedUniqueParameterAsKey {

}
