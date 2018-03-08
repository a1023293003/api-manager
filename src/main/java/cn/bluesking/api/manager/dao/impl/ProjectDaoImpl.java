package cn.bluesking.api.manager.dao.impl;

import java.util.List;

import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.dao.ProjectDao;
import cn.bluesking.api.manager.helper.DataSourceHelper;

/**
 * 项目信息数据存取实现类
 * 
 * @author 随心
 *
 */
public class ProjectDaoImpl implements ProjectDao {

    @Override
    public List<Project> listProject() {
        return DataSourceHelper.listProject();
    }

    @Override
    public Project getProject(String projectName) {
        return DataSourceHelper.getProject(projectName);
    }
    
    @Override
    public Project getProject(int pid) {
        return DataSourceHelper.getProject(pid);
    }

    @Override
    public Project saveProject(Project project) {
        project = DataSourceHelper.saveProject(project);
        DataSourceHelper.updateToProperties();
        return project;
    }

    @Override
    public Project updateProject(Project project) {
        project = DataSourceHelper.updateProject(project);
        DataSourceHelper.updateToProperties();
        return project;
    }

    @Override
    public Project deleteProject(int pid) {
        Project project = DataSourceHelper.deleteProject(pid);
        DataSourceHelper.updateToProperties();
        return project;
    }

}
