package cn.bluesking.api.manager.dao;

import java.util.List;

import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.Request;

/**
 * api信息数据存取对象接口
 * 
 * @author 随心
 *
 */
public interface ApiDao {

    /**
     * 获取所有项目信息
     * @return [List<Api>]该方法执行成功后会返回所有api对象
     */
    List<Api> listApi();
    
    /**
     * 通过请求对象获取api信息
     * 
     * @param request [Request]请求对象
     * @return [Api]方法执行查询api操作成功后会返回查询成功的api信息,执行失败后方法返回null
     */
    Api getApi(Request request);
    
    /**
     * 通过aid(唯一标识id)查询api信息
     * @param aid [int]对应一个api信息的唯一标识符
     * @return [Api]方法执行查询api操作成功后会返回查询成功的api信息,执行失败后方法返回null
     */
    Api getApi(int aid);
    
    /**
     * 添加api信息
     * @param api [Api]待添加api对象
     * @return [Api]方法执行添加api操作成功会返回添加的api信息(包含uid),执行操作失败后方法返回null
     */
    Api saveApi(Api api);
    
    /**
     * 更新api信息
     * @param api [Api]更新后的api对象
     * @return [Api]方法执行更新api操作成功后会返回更新后的api信息,执行失败后方法返回null
     */
    Api updateApi(Api api);
    
    /**
     * 通过uid(唯一标识id)删除api信息
     * @param aid [int]对应一个api信息的唯一标识符
     * @return [Api]方法执行删除api操作成功后会返回删除成功的api信息,执行失败后方法返回null
     */
    Api deleteApi(int aid);
    
}
