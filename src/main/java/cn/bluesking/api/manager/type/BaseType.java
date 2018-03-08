package cn.bluesking.api.manager.type;

/**
 * 基础数据类型接口
 * 
 * @author 随心
 *
 */
public interface BaseType {

    /**
     * 转换成字符串
     * @return
     */
    String toString();
    
    /**
     * 转换成数据类型
     * @param str [String]字符串形式的待转换数据
     * @return
     * @exception 字符串转换成对应类型数据出错
     */
    BaseType toData(String str) throws Exception;
    
    /**
     * 获取数据值
     * @return
     */
    Object getValue();
    
}
