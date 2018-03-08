package cn.bluesking.api.manager.service;

import java.util.List;

import cn.bluesking.api.manager.annotation.RequestMapping;
import cn.bluesking.api.manager.annotation.Service;
import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.bean.RequestParam;
import cn.bluesking.api.manager.constant.ContentTypeConstant;
import cn.bluesking.api.manager.constant.EncodingConstant;
import cn.bluesking.api.manager.constant.RequestMethodConstant;
import cn.bluesking.api.manager.dao.ProjectDao;
import cn.bluesking.api.manager.dao.impl.ProjectDaoImpl;
import cn.bluesking.api.manager.helper.ServiceHelper;
import cn.bluesking.api.manager.helper.ServletHelper;
import cn.bluesking.api.manager.util.CollectionUtil;
import cn.bluesking.api.manager.util.JsonUtil;

/**
 * 项目操作服务类
 * 
 * @author 随心
 *
 */
@Service
public class ProjectService {

    /** 项目持久层操作 */
    private static ProjectDao PROJECT_DAO = new ProjectDaoImpl();
    
    /**
     * 获取项目列表
     */
    @RequestMapping(path = "/project/listProject", method = RequestMethodConstant.GET)
    public static void listProject() {
        List<Project> projects = PROJECT_DAO.listProject();
        ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
        if (CollectionUtil.isEmpty(projects)) {
            ServletHelper.printToResponse("[]");
        } else {
            ServletHelper.printToResponse(JsonUtil.toJson(projects));
        }
    }
    
    /**
     * 创建项目
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/project/insertProject", method = RequestMethodConstant.GET)
    public static void insertProject(RequestParam param) {
        if (!param.containsField("projectName")) {
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            // 创建项目对象
            String projectName = param.getString("projectName");
            Project project = new Project();
            project.setProjectName(projectName);
            project = PROJECT_DAO.saveProject(project);
            // 反馈数据
            ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
            if (project == null) {
                ServiceHelper.invokeServiceMethod("/error/500", RequestMethodConstant.GET);
            } else {
                ServletHelper.printToResponse(JsonUtil.toJson(project));
            }
        }
    }
    
    /**
     * 删除项目
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/project/deleteProject", method = RequestMethodConstant.GET)
    public static void deleteProject(RequestParam param) {
        if (!param.containsField("pid")) {
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            // 删除指定pid的项目
            int pid = param.getInt("pid");
            Project project = PROJECT_DAO.deleteProject(pid);
            // 反馈数据
            ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
            if (project == null) {
                ServiceHelper.invokeServiceMethod("/error/500", RequestMethodConstant.GET);
            } else {
                ServletHelper.printToResponse(JsonUtil.toJson(project));
            }
        }
    }
    
    /**
     * 修改项目
     * 
     * @param param [RequestParam]请求参数集合
     */
    @RequestMapping(path = "/project/updateProject", method = RequestMethodConstant.GET)
    public static void updateProject(RequestParam param) {
        if (!param.containsField("projectName") || !param.containsField("pid")) {
            ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
        } else {
            // 创建项目对象
            String projectName = param.getString("projectName");
            Integer pid = param.getInt("pid");
            Project project = PROJECT_DAO.getProject(pid);
            project.setProjectName(projectName);
            project = PROJECT_DAO.updateProject(project);
            // 反馈数据
            ServletHelper.setResponseContentType(ContentTypeConstant.JSON, EncodingConstant.UTF_8);
            if (project == null) {
                ServiceHelper.invokeServiceMethod("/error/500", RequestMethodConstant.GET);
            } else {
                ServletHelper.printToResponse(JsonUtil.toJson(project));
            }
        }
    }
    
}
