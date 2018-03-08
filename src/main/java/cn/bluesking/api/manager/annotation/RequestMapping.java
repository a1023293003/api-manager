package cn.bluesking.api.manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.bluesking.api.manager.constant.RequestMethodConstant;

/**
 * 控制器注解
 * 
 * @author 随心
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * 请求路径
     */
    String path() default "";
    
    /**
     * 请求方法
     */
    String method() default RequestMethodConstant.GET;
    
}
