package cn.bluesking.api.manager.core.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bluesking.api.manager.annotation.Function;
import cn.bluesking.api.manager.annotation.NeedUniqueParameterAsKey;
import cn.bluesking.api.manager.core.Iterator;
import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;
import cn.bluesking.api.manager.helper.ClassHelper;
import cn.bluesking.api.manager.helper.ConvertHelper;
import cn.bluesking.api.manager.helper.ExceptionHelper;
import cn.bluesking.api.manager.helper.IteratorHelper;
import cn.bluesking.api.manager.type.BaseObjectType;
import cn.bluesking.api.manager.type.BaseType;
import cn.bluesking.api.manager.util.CharUtil;
import cn.bluesking.api.manager.util.CollectionUtil;
import cn.bluesking.api.manager.util.ReflectionUtil;

/**
 * 生成规则解析器
 * <pre>
 * 单纯的把输入字符串解析成为Instruction对象
 * </pre>
 * 
 * @author 随心
 *
 */
public class GenerativeRuleParser {

    /** slf4j日志配置 */
    private static final Logger _LOG = LoggerFactory.getLogger(GenerativeRuleParser.class);
    
    /** 自定义方法和方法名的映射集合 */
    private static final Map<String, List<Method>> FUNCTION_MAP = new HashMap<String, List<Method>>();
    
    /**
     * 对于递增方法等需要保存上一次同一生成规则解析时调用方法的返回值的方法,
     * 通过为方法多传入一个唯一参数作为key,用Map来维护一个变量空间.该变量为
     * 计数器,用于生成key的唯一参数.
     */
    private static final AtomicLong COUNTER = new AtomicLong(0L);
    
    /** 用于存储需要传入一个唯一键值的方法标识(方法名 + 方法参数个数)集合 */
    private static final Set<String> NEED_KEY_METHOD_SET = new HashSet<String>();
    
    /**
     * 检验参数类型是否合法
     * <pre>
     * 参数必须是字符串或BaseType的子类
     * </pre>
     * 
     * @param classes [Class<?>[]]参数类型数组
     * @return
     */
    private static boolean checkParameterTypes(Class<?>[] classes) {
        for (Class<?> cls : classes) {
            if (!BaseType.class.isAssignableFrom(cls) && String.class != cls) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 添加方法到map中
     * 
     * @param method
     */
    private static void addMethodToMap(Method method) {
        // key是方法名 + 方法参数个数
        String key = method.getName() + method.getParameterCount();
        List<Method> methodList = FUNCTION_MAP.get(key);
        if (methodList == null) {
            methodList = new ArrayList<Method>();
            FUNCTION_MAP.put(key, methodList);
        }
        methodList.add(method);
    }
    
    static {
        // 带有函数注解的类的Class对象
        Set<Class<?>> classSet = ClassHelper.getClassSetByAnnotation(Function.class);
        List<Method> methods;
        for (Class<?> cls : classSet) {
            System.out.println(cls.getName());
            // 获取Class类中被Function注解的方法
            methods = ReflectionUtil.getMethodByAnnotation(cls, Function.class);
            for (Method method : methods) {
                
                if (!Modifier.isStatic(method.getModifiers())) {
                    // 校验方法类型
                    ExceptionHelper.printAndExit(method.getName() + "方法必须是静态方法", null);
                    
                } else if (method.getReturnType() == void.class && 
                        !BaseType.class.isAssignableFrom(method.getReturnType()) && 
                        String.class != method.getReturnType()) {
                    // 校验返回值类型
                    ExceptionHelper.printAndExit(method.getName() + 
                            "方法返回值不能为空且返回值必须是字符串或BaseType的子类", null);
                    
                } else if (!checkParameterTypes(method.getParameterTypes())) {
                    // 校验参数类型
                    ExceptionHelper.printAndExit(method.getName() + 
                            "方法参数值必须是字符串或BaseType的子类", null);
                    
                } else {
                    
                    // 方法存入map中
                    addMethodToMap(method);
                    _LOG.debug("载入方法:" + method.getName() + "(" + method.getParameterCount() + ")");
                    // 如果该方法被标记需要唯一参数作为独立变量空间的键值
                    if (method.isAnnotationPresent(NeedUniqueParameterAsKey.class)) {
                        NEED_KEY_METHOD_SET.add(method.getName() + (method.getParameterCount() - 1 ));
                    }
                }
            }
        }
    }
    
    /**
     * 获取一个规则解析器
     * 
     * @return
     */
    public static GenerativeRuleParser getParser() {
        return InnerParserFactory.getParser();
    }
    
    /** 私有化构造方法 */
    private GenerativeRuleParser() {};
    
    /**
     * 用于实现懒汉式单例模式的内部静态工厂类
     * 
     * @author sequoiadb
     *
     */
    private static final class InnerParserFactory {
        
        /** 规则解析器 */
        private static final GenerativeRuleParser PARSER;
        
        static {
            PARSER = new GenerativeRuleParser();
        }
        
        public static GenerativeRuleParser getParser() {
            return PARSER;
        }
        
    }
    
    /**
     * 解析JSON中定义生成规则的Value,并返回相应的Instruction(指令对象)
     * 
     * @param source [String]定义生成规则的数据源
     * @return [Instruction]根据规则生成的Instruction对象
     * @throws IllegalArgumentException 解析传入数据过程中出现错误会抛出这个异常
     */
    public Instruction parser(String source) {
        // 创建迭代器
        Iterator<Character> iterator = 
                new Iterator<Character>(CharUtil.toCharacterArray(source.toCharArray()));
        // 无操作根指令
        Instruction rootInstruction = new Instruction(Instruction.NONE);
        // 解析生成规则
        getValue(iterator, rootInstruction);
        return rootInstruction;
    }
    
    /**
     * 解析JSON中定义生成规则的Value,并返回规则解析后的值(字符串形式的)
     * 
     * @param source          [String]定义生成规则的数据源
     * @param rootInstruction [Instruction]根指令对象
     * @return [String]根据规则生成的value值
     * @throws IllegalArgumentException 解析传入数据过程中出现错误会抛出这个异常
     */
    public String parser(String source, Instruction rootInstruction) {
        // 无操作根指令
        rootInstruction.setAction(Instruction.NONE);
        // 创建迭代器
        Iterator<Character> iterator = 
                new Iterator<Character>(CharUtil.toCharacterArray(source.toCharArray()));
        // 解析生成规则
        Object result = getValue(iterator, rootInstruction);
        // 针对不同的返回值执行相应操作
        if (BaseObjectType.class.isAssignableFrom(result.getClass())) {     // Entry
            return ((BaseObjectType) result).toEntry().toString();
        } else if (BaseType.class.isAssignableFrom(result.getClass())) {    // value
            return ((BaseType) result).getValue() + "";
        } else {                                                            // String
            return result.toString();
        }
    }
    
    /**
     * 解析并获取value值
     * 
     * @param iterator        [Iterator<Character>]待解析字符数组迭代器
     * @param rootInstruction [Instruction]无操作根指令
     * @return
     * @throws IllegalArgumentException 解析传入数据过程中出现错误会抛出这个异常
     */
    private Object getValue(Iterator<Character> iterator, Instruction rootInstruction) {
        // 指令操作中字符串参数缓存
        StringBuilder buf = new StringBuilder();
        // 前缀
        StringBuilder prefix = new StringBuilder();
        while (iterator.isNotEnd()) {
            // 初始化缓存
            buf.setLength(0);
            // 上一个字符是转义符
            boolean precedingBackslash = false;
            char c;
            // 找到一个函数调用的位置
            for (; iterator.isNotEnd(); iterator.next()) {
                c = iterator.getElement();
                if (precedingBackslash) {
                    precedingBackslash = false;
                    buf.append(c);
                } else if (c == '\\') {
                    precedingBackslash = true;
                } else if (c == '$') {
                    break;
                } else {
                    buf.append(c);
                }
            }
            // 存在前缀字符串,添加到指令操作参数中
            if (buf.length() > 0) {
                rootInstruction.addParameter(buf.toString());
                prefix.append(buf.toString());
            }
            // 字符串解析
            if (iterator.isEnd()) {
                // 字符数组遍历完毕
                break;
            } else {
                // 方法调用指令
                Instruction methodInstruction = new Instruction(Instruction.METHOD);
                // 获取方法返回值
                Object value = getFunctionReturnValue(iterator, methodInstruction);
                if (prefix.length() <= 0 && iterator.isEnd()) {
                    // 无前后缀
                    // 直接将方法调用指令附加到根指令下面
                    rootInstruction.addParameter(methodInstruction);
                    return value;                                  // BaseType、String
                } else {
                    // 有前缀或后缀
                    // 存在前后缀的时候是拼接字符串指令
                    if (rootInstruction.getAction() == Instruction.NONE) {
                        rootInstruction.setAction(Instruction.APPEND);
                    }
                    // 根指令添加一个参数为方法调用指令
                    rootInstruction.addParameter(methodInstruction);
                    prefix.append(value); // BaseType已经重写toString了
                }
            }
        }
        return prefix.toString();                                  // String
    }

    /**
     * 获取方法参数值,专门用来读取函数双括号中间参数值的方法
     * 
     * @param iterator         [Iterator<Character>]待解析字符数组迭代器
     * @param paramInstruction [Instruction]参数获取操作指令
     * @return
     * @throws IllegalArgumentException 解析传入数据过程中出现错误会抛出这个异常
     */
    private Object getParammeter(Iterator<Character> iterator, Instruction paramInstruction) {
        // 记录调用方法时的游标,用于输出错误信息
        int currentCursor = iterator.getCursor();
        // 指令操作中字符串参数缓存
        StringBuilder buf = new StringBuilder();
        // 前缀
        StringBuilder prefix = new StringBuilder();
        while (iterator.isNotEnd()) {
            // 初始化缓存
            buf.setLength(0);
            // 上一个字符是转义符
            boolean precedingBackslash = false;
            char c;
            // 找到一个函数调用的位置
            for (; iterator.isNotEnd(); iterator.next()) {
                c = iterator.getElement();
                if (precedingBackslash) {
                    precedingBackslash = false;
                    buf.append(c);
                } else if (c == ',' || c == ')') {
                    iterator.next();
                    // 返回之前先把截取到的字符串存到指令节点中
                    paramInstruction.addParameter(buf.toString());
                    return prefix.append(buf).toString();
                } else if (c == '\\') {
                    precedingBackslash = true;
                } else if (c == '$') {
                    prefix.append(buf);
                    break;
                } else {
                    buf.append(c);
                }
            }
            if (iterator.isEnd()) {
                throw new IllegalArgumentException("配置规则不合法,数组提前遍历完毕,获取方法参数值失败!"
                        + IteratorHelper.currentSummary(iterator, currentCursor));
            } else {
                // 存在前缀字符串,添加到参数获取指令的参数中
                if (buf.length() > 0) {
                    paramInstruction.addParameter(buf.toString());
                }
                // 方法调用操作指令
                Instruction methodInstruction = new Instruction(Instruction.METHOD);
                // 获取方法返回值
                Object value = getFunctionReturnValue(iterator, methodInstruction);
                if (iterator.isEnd()) {
                    throw new IllegalArgumentException("配置规则不合法,数组提前遍历完毕,获取方法返回值失败!"
                            + IteratorHelper.currentSummary(iterator, currentCursor), null);
                } else if (prefix.length() <= 0 && 
                        (iterator.getElement() == ',' || iterator.getElement() == ')')) {
                    // 无前后缀
                    // 直接将方法调用指令附加到参数获取根指令下面
                    paramInstruction.addParameter(methodInstruction);
                    iterator.next();
                    return value;                                  // BaseType、String
                } else {
                    // 有前后缀
                    // 拼接字符串指令
                    if (paramInstruction.getAction() == Instruction.NONE) {
                        paramInstruction.setAction(Instruction.APPEND);
                    }
                    // 参数获取指令添加一个方法调用指令作为参数
                    paramInstruction.addParameter(methodInstruction);
                    prefix.append(value); // BaseType已经重写toString了
                }
            }
        }
        return prefix.toString();                                  // String
    }
    
    /**
     * 遍历执行方法
     * 
     * @param methodName  [String]方法名
     * @param paramBuf    [List<Object>]方法参数
     * @param instruction [Instruction]操作指令
     * @return
     */
    private Object invokeMethods(String methodName, List<Object> paramBuf, Instruction instruction) {
        // 键值对中的键值为:方法名称 + 方法参数个数
        String key = methodName + paramBuf.size();
        // 判断当前方法是否需要唯一参数作为标识符
        if (NEED_KEY_METHOD_SET.contains(key)) {
            // 构建唯一标识符
            String group = "#" + COUNTER.addAndGet(1);
            // 参数集合添加新参数
            paramBuf.add(group);
            // 方法调用参数新增参数获取指令
            Instruction paramInstruction = new Instruction(Instruction.NONE);
            paramInstruction.addParameter(group);
            instruction.addParameter(paramInstruction);
            // 重构key
            key = methodName + paramBuf.size();
        }
        List<Method> methodList = FUNCTION_MAP.get(key);
        if (CollectionUtil.isEmpty(methodList)) {
            throw new IllegalArgumentException(
                    paramBuf.size() + "个参数的方法" + methodName + "不存在!");
        } else {
            Object[] params = new Object[paramBuf.size()];
            // 标记参数,用于标记参数转换是否成功
            boolean vaild;
            for (Method method : methodList) {
                Class<?>[] paramClasses = method.getParameterTypes();
                vaild = true; // 默认参数转换成功,可执行方法
                for (int i = 0; i < params.length; i ++) {
                    try {
                        params[i] = ConvertHelper.convertParameter(paramClasses[i], paramBuf.get(i));
                    } catch (IllegalArgumentException e) {
                        vaild = false; // 参数转换发生异常,跳过当前方法
                        break;
                    }
                }
                if (vaild) {
                    try {
                        return method.invoke(null, params);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(method.getName() + "方法执行过程中出错!", e);
                    } finally {
                        // 方法调用指令,设置方法对象
                        instruction.setMethod(method);
                    }
                }
            }
            throw new IllegalArgumentException("无法找到可有效执行的方法名为" + methodName + 
                    ",参数个数为" + paramBuf.size() + "的方法!", null);
        }
    }
    
    /**
     * 执行函数
     * 
     * @param iterator          [Iterator<Character>]待解析字符数组迭代器
     * @param methodInstruction [Instruction]方法调用操作指令
     * @return
     * @throws IllegalArgumentException 解析传入数据过程中出现错误会抛出这个异常
     */
    private Object getFunctionReturnValue(Iterator<Character> iterator, Instruction methodInstruction) {
        // 记录调用方法时的游标,用于输出错误信息
        int currentCursor = iterator.getCursor();
        if (iterator.getElement() != '$') {
            throw new IllegalArgumentException("解析函数出错!刚刚还看到的'$'符号转眼就不见了"
                    + IteratorHelper.currentSummary(iterator, currentCursor));
        } else {
            // 函数名称(也是函数对应的方法名称)
            StringBuilder methodName = new StringBuilder();
            // 上一个字符是转义符
            boolean precedingBackslash = false;
            char c;
            // 截取函数名
            for (iterator.next(); iterator.isNotEnd(); iterator.next()) {
                c = iterator.getElement();
                if (precedingBackslash) {
                    precedingBackslash = false;
                    methodName.append(c);
                } else if (c == '\\') {
                    precedingBackslash = true;
                } else if (c == '(') {
                    // 函数名称截取结束标记
                    iterator.next();
                    break;
                } else {
                    methodName.append(c);
                }
            }
            if (iterator.isEnd()) {
                throw new IllegalArgumentException("配置规则不合法,数组提前遍历完毕,获取函数名出错!"
                        + IteratorHelper.currentSummary(iterator, currentCursor));
            } else {
                // 获取方法参数值
                List<Object> paramBuf = new ArrayList<Object>();
                Object p;
                // 读取参数
                // 上一个字符是方法结束符(')')
                while (iterator.lastElementNext() != ')') {
                    if (iterator.isEnd()) {
                        throw new IllegalArgumentException("配置规则不合法,数组提前遍历完毕,未能成功读取方法参数"
                                + IteratorHelper.currentSummary(iterator, currentCursor));
                    } else {
                        // 创建参数获取指令节点
                        Instruction paramInstruction = new Instruction(Instruction.NONE);
                        p = getParammeter(iterator, paramInstruction);
                        if (p.getClass() == String.class && ((String) p).trim().length() == 0) {
                            // 参数为空
                        } else {
                            paramBuf.add(p);
                            // 方法调用指令添加参数
                            methodInstruction.addParameter(paramInstruction);
                        }
                    }
                }
                // 执行方法,并为方法调用指令存入调用方法对象
                try {
                    return invokeMethods(methodName.toString(), paramBuf, methodInstruction);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.getMessage()
                            + IteratorHelper.currentSummary(iterator, currentCursor), e);
                }
            }
        }
    }
    
    /**
     * 指令
     * 
     * @author sequoiadb
     *
     */
    public static class Instruction {
        
        /** 无操作 */
        public static final int NONE   = 1;
        /** 字符串拼接 */
        public static final int APPEND = 2;
        /** 方法调用 */
        public static final int METHOD = 3;
        
        /** 操作类型 */
        private int action;
        
        /** 参数 */
        private List<Object> parameters = new ArrayList<Object>();
        
        /** 操作方法对象 */
        private Method method;

        public Instruction(int action) {
            this.action = action;
        }
        
        public int getAction() {
            return action;
        }
        
        public void setAction(int action) {
            this.action = action;
        }

        public void addParameter(Object parameter) {
            this.parameters.add(parameter);
        }

        public void setMethod(Method method) {
            this.method = method;
        }
        
        /**
         * 处理各种数据类型返回值
         * @param value [Object]待处理返回值
         * @return
         */
        private Object getValue(Object value) {
            Class<?> cls = value.getClass();
            if (cls == String.class) {
                return value;
            } else if (cls == Instruction.class) {
                return ((Instruction) value).getValue();
            } else {
                throw new IllegalArgumentException("指令节点中的参数值必须全部都是字符串或者子节点!");
            }
        }
        
        /**
         * 通过遍历该指令节点及其子节点,得到这一系列指令对应的操作的返回值(已解析返回值并转换为字符串)
         * @return
         */
        public String getStringValue() {
            // 获取指令集合执行后的结果值
            Object result = getObjectValue();
            Class<?> cls = result.getClass();
            if (cls == Entry.class) {               // Entry
                return ((Entry) result).toString();
            } else if (cls == String.class) {       // String
                return result.toString();
            } else {                                // integer,Double,Decimal...
                return result + "";
            }
        }
        
        /**
         * 通过遍历该指令节点及其子节点,得到这一系列指令对应的操作的返回值(已解析返回值)
         * @return
         */
        public Object getObjectValue() {
            // 获取指令集合执行后的结果值
            Object result = getValue();
            // 针对不同的返回值执行相应操作
            if (BaseObjectType.class.isAssignableFrom(result.getClass())) {     // Entry
                return ((BaseObjectType) result).toEntry();
            } else if (BaseType.class.isAssignableFrom(result.getClass())) {    // value
                return ((BaseType) result).getValue();
            } else {                                                            // String
                return result;
            }
        }
        
        /**
         * 通过遍历该指令节点及其子节点,得到这一系列指令对应的操作的返回值(为解析返回值)
         * @return
         */
        public Object getValue() {
            if (action == NONE) {
                // 无操作,直接返回值
                return this.getValue(this.parameters.get(0));
            } else if (action == APPEND) {
                // 字符串拼接
                StringBuilder buf = new StringBuilder();
                for (Object param : this.parameters) {
                    buf.append(this.getValue(param));
                }
                return buf.toString();
            } else if (action == METHOD) {
                // 方法调用
                Object[] params = new Object[this.parameters.size()];
                Class<?>[] paramTypes = this.method.getParameterTypes();
                // 参数类型转换
                for (int i = 0; i < this.parameters.size(); i ++) {
                    params[i] = ConvertHelper.convertParameter(
                            paramTypes[i], 
                            this.getValue(this.parameters.get(i)));
                }
                try {
                    return this.method.invoke(null, params);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("尝试调用" + method.getName()
                            + "方法,但无法访问该方法!", e);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("尝试调用" + method.getName()
                            + "方法,但参数不合法!", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("尝试调用" + method.getName() + "方法,但是失败了!", e);
                }
            } else {
                throw new IllegalArgumentException("指令节点的类型不合法!" + this.action);
            }
        }
    }
}
