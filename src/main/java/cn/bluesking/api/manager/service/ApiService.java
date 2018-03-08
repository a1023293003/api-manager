package cn.bluesking.api.manager.service;

import java.util.ArrayList;
import java.util.List;

import cn.bluesking.api.manager.annotation.RequestMapping;
import cn.bluesking.api.manager.annotation.Service;
import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.ApiFileParam;
import cn.bluesking.api.manager.bean.ApiFormParam;
import cn.bluesking.api.manager.bean.ApiParam;
import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.bean.RequestParam;
import cn.bluesking.api.manager.constant.ContentTypeConstant;
import cn.bluesking.api.manager.constant.EncodingConstant;
import cn.bluesking.api.manager.constant.RequestMethodConstant;
import cn.bluesking.api.manager.dao.ApiDao;
import cn.bluesking.api.manager.dao.ProjectDao;
import cn.bluesking.api.manager.dao.impl.ApiDaoImpl;
import cn.bluesking.api.manager.dao.impl.ProjectDaoImpl;
import cn.bluesking.api.manager.helper.ServiceHelper;
import cn.bluesking.api.manager.helper.ServletHelper;
import cn.bluesking.api.manager.util.JsonUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * api操作服务类
 * 
 * @author 随心
 *
 */
@Service
public class ApiService {

    /** 项目持久层操作 */
    private static ProjectDao PROJECT_DAO = new ProjectDaoImpl();
    
    /** api持久层操作 */
    private static ApiDao API_DAO = new ApiDaoImpl();
    
    /**
     * 创建api
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/api/insertApi", method = RequestMethodConstant.POST)
    public static void insertApi(RequestParam param) {
        if (!param.containsField("pid") || 
                !param.containsField("requestPath") ||
                !param.containsField("requestMethod") || 
                !param.containsField("responseContentConfiguration")) {
            // 必要参数校验
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            // 校验pid是否合法
            Integer pid = param.getInt("pid");
            Project project = PROJECT_DAO.getProject(pid);
            if (project == null) {
                ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
            } else {
                // 创建api
                Api api = new Api();
                api.setPid(pid);
                api.setRequestPath(param.getString("requestPath"));
                api.setRequestMethod(param.getString("requestMethod"));
                // html实体编码
                api.encodeAndsetTemplate(param.getString("responseContentConfiguration"));
                if (param.containsField("encoding")) { api.setEncoding(param.getString("encoding")); }
                if (param.containsField("contentType")) { api.setContentType(param.getString("contentType")); }
                
                // api表单参数
                List<ApiFormParam> formParamList = new ArrayList<>();
                String necessaryFormParam = param.getString("necessaryFormParam");
                // 必要表单参数
                if (StringUtil.isNotEmpty(necessaryFormParam)) {
                    String[] paramNames = necessaryFormParam.split(";");
                    for (String paramName : paramNames) {
                        formParamList.add(new ApiFormParam(paramName, null, true));
                    }
                } else {} // do nothing
                // 可选表单参数
                String formParam = param.getString("formParam");
                if (StringUtil.isNotEmpty(formParam)) {
                    String[] paramNames = formParam.split(";");
                    for (String paramName : paramNames) {
                        formParamList.add(new ApiFormParam(paramName, null, false));
                    }
                } else {} // do nothing
                
                // api文件参数
                List<ApiFileParam> fileParamList = new ArrayList<>();
                String necessaryFileParam = param.getString("necessaryFileParam");
                // 必要文件参数
                if (StringUtil.isNotEmpty(necessaryFileParam)) {
                    String[] paramNames = necessaryFileParam.split(";");
                    for (String paramName : paramNames) {
                        fileParamList.add(new ApiFileParam(paramName, null, true));
                    }
                } else {} // do nothing
                // 可选文件参数
                String fileParam = param.getString("fileParam");
                if (StringUtil.isNotEmpty(fileParam)) {
                    String[] paramNames = fileParam.split(";");
                    for (String paramName : paramNames) {
                        fileParamList.add(new ApiFileParam(paramName, null, false));
                    }
                } else {} // do nothing
                
                // api需要的参数
                if (formParamList.size() > 0 || fileParamList.size() > 0) {
                    api.setParam(new ApiParam(formParamList, fileParamList));
                } else {
                    api.setParam(null);
                }
                
                // 持久化存储api
                api = API_DAO.saveApi(api);
                ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
                ServletHelper.printToResponse(JsonUtil.toJson(api));
            }
        }
    }
    
    /**
     * 删除api
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/api/deleteApi", method = RequestMethodConstant.GET)
    public static void deleteApi(RequestParam param) {
        if (!param.containsField("aid")) {
            // 参数校验
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            Integer aid = param.getInt("aid");
            Api api = API_DAO.deleteApi(aid);
            if (api == null) {
                // 删除失败
                ServiceHelper.invokeServiceMethod("/error/500", RequestMethodConstant.GET);
            } else {
                ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
                ServletHelper.printToResponse(JsonUtil.toJson(api));
            }
        }
    }
    
    /**
     * 更新api
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/api/updateApi", method = RequestMethodConstant.POST)
    public static void updateApi(RequestParam param) {
        if (!param.containsField("aid") || 
                !param.containsField("requestPath") ||
                !param.containsField("requestMethod") || 
                !param.containsField("responseContentConfiguration")) {
            // 必要参数校验
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            // 校验aid是否合法
            Integer aid = param.getInt("aid");
            Api api = API_DAO.getApi(aid);
            if (api == null) {
                ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
            } else {
                // 修改api
                api.setRequestPath(param.getString("requestPath"));
                api.setRequestMethod(param.getString("requestMethod"));
                // html实体编码
                api.encodeAndsetTemplate(param.getString("responseContentConfiguration"));
                if (param.containsField("encoding")) { api.setEncoding(param.getString("encoding")); }
                if (param.containsField("contentType")) { api.setContentType(param.getString("contentType")); }
                
                // api表单参数
                List<ApiFormParam> formParamList = new ArrayList<>();
                String necessaryFormParam = param.getString("necessaryFormParam");
                // 必要表单参数
                if (StringUtil.isNotEmpty(necessaryFormParam)) {
                    String[] paramNames = necessaryFormParam.split(";");
                    for (String paramName : paramNames) {
                        formParamList.add(new ApiFormParam(paramName, null, true));
                    }
                } else {} // do nothing
                // 可选表单参数
                String formParam = param.getString("formParam");
                if (StringUtil.isNotEmpty(formParam)) {
                    String[] paramNames = formParam.split(";");
                    for (String paramName : paramNames) {
                        formParamList.add(new ApiFormParam(paramName, null, false));
                    }
                } else {} // do nothing
                
                // api文件参数
                List<ApiFileParam> fileParamList = new ArrayList<>();
                String necessaryFileParam = param.getString("necessaryFileParam");
                // 必要文件参数
                if (StringUtil.isNotEmpty(necessaryFileParam)) {
                    String[] paramNames = necessaryFileParam.split(";");
                    for (String paramName : paramNames) {
                        fileParamList.add(new ApiFileParam(paramName, null, true));
                    }
                } else {} // do nothing
                // 可选文件参数
                String fileParam = param.getString("fileParam");
                if (StringUtil.isNotEmpty(fileParam)) {
                    String[] paramNames = fileParam.split(";");
                    for (String paramName : paramNames) {
                        fileParamList.add(new ApiFileParam(paramName, null, false));
                    }
                } else {} // do nothing
                
                // api需要的参数
                if (formParamList.size() > 0 || fileParamList.size() > 0) {
                    api.setParam(new ApiParam(formParamList, fileParamList));
                } else {
                    api.setParam(null);
                }
                
                // 持久化存储api
                api = API_DAO.updateApi(api);
                ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
                ServletHelper.printToResponse(JsonUtil.toJson(api));
            }
        }
    }
    
}
