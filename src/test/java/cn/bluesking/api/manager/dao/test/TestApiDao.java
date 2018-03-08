package cn.bluesking.api.manager.dao.test;

import java.util.List;

import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.constant.ContentTypeConstant;
import cn.bluesking.api.manager.constant.EncodingConstant;
import cn.bluesking.api.manager.constant.RequestMethodConstant;
import cn.bluesking.api.manager.dao.ApiDao;
import cn.bluesking.api.manager.dao.impl.ApiDaoImpl;
import cn.bluesking.api.manager.dao.impl.ProjectDaoImpl;
import cn.bluesking.api.manager.util.MathUtil;

public class TestApiDao {

    private static final ApiDao apiDao = new ApiDaoImpl();
    
    private static Project project;

    public static void init() {
        project = new Project("testApi", null);
        new ProjectDaoImpl().saveProject(project);
        project = new ProjectDaoImpl().getProject("testApi");
    }
    
    public static void testSaveApi() {
        Api api = new Api(null, project.getPid(), RequestMethodConstant.GET, 
                "/testApi/index" + MathUtil.getRandom(1, 1000) + ".html", null, EncodingConstant.UTF_8, 
                ContentTypeConstant.JSON, "{\"a\":\"$randomString($randomInt(10,100))\"}");
        api = apiDao.saveApi(api);
        System.out.println("【存储api对象】" + api);
    }
    
    public static void testUpdateApi() {
        Api api = apiDao.listApi().get(0);
        api.encodeAndsetTemplate("{\"b\":\"$randomInt(-10,-100)\"}");
        api = apiDao.updateApi(api);
        System.out.println("【修改api对象】" + api.toString());
    }
    
    public static void testDeleteApi() {
        List<Api> apiList = apiDao.listApi();
        for (Api api : apiList) {
            api = apiDao.deleteApi(api.getAid());
            System.out.println("【删除api对象】" + api);
        }
    }
    
    public static void main(String[] args) {
//        init();
//        testDeleteApi();
//        testSaveApi();
//        testUpdateApi();
        apiDao.deleteApi(4);
    }
    
}
