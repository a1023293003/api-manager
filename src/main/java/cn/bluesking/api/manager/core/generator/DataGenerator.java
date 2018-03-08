package cn.bluesking.api.manager.core.generator;

import java.util.concurrent.ConcurrentHashMap;

import cn.bluesking.api.manager.core.parser.GenerativeRuleParser;
import cn.bluesking.api.manager.core.parser.GenerativeRuleParser.Instruction;

/**
 * 数据生成器
 * 
 * @author 随心
 *
 */
public class DataGenerator {
    
    /** 函数解析器 */
    private static final GenerativeRuleParser GENERATIVE_RULE__PARSER;
    
    /** 输入数据和其解析之后指令的映射集合 */
    private static final ConcurrentHashMap<String, Instruction> INSTRUCTION_MAP = 
            new ConcurrentHashMap<String, Instruction>(16);
    
    /** 数据之间默认的分隔符 */
    private static final String DEFAULT_SEPARATOR = "\n";
    
    static {
        GENERATIVE_RULE__PARSER = GenerativeRuleParser.getParser();
    }
    
    /**
     * 生成JSON数据
     * 
     * @param source [String]数据源
     * @param count  [int]生成数据条数
     * @return
     */
    public static String generate(String source, int count) {
        return generate(source, count, DEFAULT_SEPARATOR);
    }
    
    /**
     * 生成JSON数据
     * 
     * @param source    [String]数据源
     * @param count     [int]生成数据条数
     * @param separator [String]数据分隔符,每两条json数据之间的分隔符,默认是"\n"
     * @return
     */
    public static String generate(String source, int count, String separator) {
        if (count < 0) {
            throw new IllegalArgumentException("生成数据个数参数count必须是非负数");
            
        } else if (source == null) {
            throw new NullPointerException("数据源参数source不可以为null");
            
        } else if (separator == null) {
            throw new NullPointerException("数据之间的分隔符参数separator不可以为null");
            
        } else {
            
            StringBuffer resultBuf = new StringBuffer("");
            // 获取数据源解析后的根指令
            Instruction rootInstruction = getInstruction(source, resultBuf);
            count --;
            // 循环生成count条数据
            while (count -- > 0) {
                if (resultBuf.length() > 0) {
                    // 数据之间的分隔符
                    resultBuf.append(separator);
                }
                resultBuf.append(rootInstruction.getStringValue());
            }
            return resultBuf.toString();
        }
    }
    
    /**
     * 根据数据源获取数据源解析后的根指令对象
     * 
     * @param source [String]数据源
     * @return [Instruction]
     */
    private static Instruction getInstruction(String source, StringBuffer buf) {
        if (INSTRUCTION_MAP.containsKey(source)) {
            return INSTRUCTION_MAP.get(source);
            
        } else {
            
            Instruction rootInstruction = new Instruction(Instruction.NONE);
            // 解析函数的时候会执行一次
            String result = GENERATIVE_RULE__PARSER.parser(source, rootInstruction);
            buf.append(result);
            return rootInstruction;
        }
    }
    
}
