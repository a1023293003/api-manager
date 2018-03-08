package cn.bluesking.api.manager.dao;

import java.util.List;

import cn.bluesking.api.manager.bean.Project;

/**
 * 项目信息数据存取对象接口
 * 
 * @author 随心
 *
 */
public interface ProjectDao {

    /**
     * 获取所有项目信息
     * @return [List<Project>]该方法执行成功后会返回所有项目对象
     */
    List<Project> listProject();
    
    /**
     * 通过项目名称获取项目信息
     * 
     * @param projectName [String]项目名称
     * @return [Project]方法执行查询项目操作成功后会返回查询成功的项目信息,执行失败后方法返回null
     */
    Project getProject(String projectName);
    
    /**
     * 通过pid(唯一标识id)查询项目信息
     * @param pid [int]对应一个项目信息的唯一标识符
     * @return [Project]方法执行查询项目操作成功后会返回查询成功的项目信息,执行失败后方法返回null
     */
    Project getProject(int pid);
    
    /**
     * 添加项目信息
     * @param project [Project]待添加项目对象
     * @return [Project]方法执行添加项目操作成功会返回添加的项目信息(包含pid),执行操作失败后方法返回null
     */
    Project saveProject(Project project);
    
    /**
     * 更新项目信息
     * @param project [Project]更新后的项目对象
     * @return [Project]方法执行更新项目操作成功后会返回更新后的项目信息,执行失败后方法返回null
     */
    Project updateProject(Project project);
    
    /**
     * 通过pid(唯一标识id)删除项目信息
     * @param pid [int]对应一个项目信息的唯一标识符
     * @return [Project]方法执行删除项目操作成功后会返回删除成功的项目信息,执行失败后方法返回null
     */
    Project deleteProject(int pid);
    
}
