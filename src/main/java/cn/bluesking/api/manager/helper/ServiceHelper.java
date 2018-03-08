package cn.bluesking.api.manager.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bluesking.api.manager.annotation.Service;
import cn.bluesking.api.manager.annotation.RequestMapping;
import cn.bluesking.api.manager.bean.Request;
import cn.bluesking.api.manager.bean.RequestParam;

/**
 * 服务助手类
 * 
 * @author 随心
 *
 */
public final class ServiceHelper {

    /** slf4j日志配置 */
    private static final Logger _LOG = LoggerFactory.getLogger(ServiceHelper.class);
    
    /** 请求对象和实现方法之间的映射 */
    private static final Map<Request, Method> REQUEST_METHOD_MAP;
    
    static {
        REQUEST_METHOD_MAP = new HashMap<>();
        Set<Class<?>> classSet = ClassHelper.getClassSetByAnnotation(Service.class);
        Method[] methods;
        for (Class<?> cls : classSet) {
            System.out.println(cls.getName());
            methods = cls.getMethods();
            for (Method method : methods) {
                if (checkMethod(method)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    Request request = new Request(requestMapping.path(), requestMapping.method());
                    _LOG.debug(request.toString() + " : " + method.getName());
                    REQUEST_METHOD_MAP.put(request, method);
                }
            }
        }
    }
    
    /**
     * 检查方法是否合法(带有指定注解、参数为空或唯一、返回值为void、静态方法)
     * 
     * @param method [Method]待检查方法
     * @return
     */
    private static boolean checkMethod(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            if (Modifier.isStatic(method.getModifiers())) {
                if (method.getReturnType() == void.class) {
                    Class<?>[] classes = method.getParameterTypes();
                    if ((method.getParameterCount() == 1 && classes[0] == RequestParam.class) || 
                            method.getParameterCount() == 0) {
                        return true;
                    } else {
                        // 方法参数个数不为1或参数类型不为RequestParam
                        return false;
                    }
                } else {
                    // 返回值不会void
                    return false;
                }
            } else {
                // 不是静态方法
                return false;
            }
        } else {
            // 没有指定注解
            return false;
        }
    }
    
    /**
     * 判断是否存在指定请求对象的服务实现方法
     * 
     * @param request [Request]请求对象
     * @return [boolean]如果存在当前请求对应的服务实现方法,则该方法返回true,否则方法返回false
     */
    public static boolean containsService(Request request) {
        return REQUEST_METHOD_MAP.containsKey(request);
    }
    
    /**
     * 调用指定请求对应的服务实现方法
     * 
     * @param request [Request]请求对象
     * @param param   [RequestParam]请求参数
     */
    public static void invokeServiceMethod(Request request, RequestParam param) {
        if (REQUEST_METHOD_MAP.containsKey(request)) {
            Method method = REQUEST_METHOD_MAP.get(request);
            try {
                if (method.getParameterCount() == 0) {
                    method.invoke(null);
                } else {
                    method.invoke(null, param);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                _LOG.error("方法" + method.getName() + "调用失败！" + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            // do nothing
            _LOG.debug("服务实现容器中没有找到当前请求对应的实现方法!");
        }
    }
    
    /**
     * 调用指定请求对应的服务实现方法
     * 
     * @param requestPath   [String]请求路径
     * @param requestMethpd [String]请求方法
     */
    public static void invokeServiceMethod(String requestPath, String requestMethod) {
        Request request = new Request(requestPath, requestMethod);
        _LOG.debug("调用service" + request.toString());
        invokeServiceMethod(request, null);
    }
    
}
