package cn.bluesking.api.manager.dao.impl;

import java.util.List;

import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.Request;
import cn.bluesking.api.manager.dao.ApiDao;
import cn.bluesking.api.manager.helper.DataSourceHelper;

/**
 * api信息数据存取对实现类
 * 
 * @author 随心
 *
 */
public class ApiDaoImpl implements ApiDao {

    @Override
    public List<Api> listApi() {
        return DataSourceHelper.listApi();
    }

    @Override
    public Api getApi(Request request) {
        return DataSourceHelper.getApi(request);
    }
    
    @Override
    public Api getApi(int uid) {
        return DataSourceHelper.getApi(uid);
    }

    @Override
    public Api saveApi(Api api) {
        api = DataSourceHelper.saveApi(api);
        DataSourceHelper.updateToProperties();
        return api;
    }

    @Override
    public Api updateApi(Api api) {
        api = DataSourceHelper.updateApi(api);
        DataSourceHelper.updateToProperties();
        return api;
    }

    @Override
    public Api deleteApi(int aid) {
        Api api = DataSourceHelper.deleteApi(aid);
        DataSourceHelper.updateToProperties();
        return api;
    }

}
