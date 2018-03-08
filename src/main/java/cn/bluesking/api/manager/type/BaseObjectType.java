package cn.bluesking.api.manager.type;

import cn.bluesking.api.manager.core.parser.BaseJsonParser.Entry;

/**
 * 基础对象数据类型接口
 * 
 * @author 随心
 *
 */
public interface BaseObjectType extends BaseType {

    /**
     * 获取数据对应的节点
     * @return
     */
    Entry toEntry();
    
}
