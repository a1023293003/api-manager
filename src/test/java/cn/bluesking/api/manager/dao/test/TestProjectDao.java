package cn.bluesking.api.manager.dao.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.dao.ProjectDao;
import cn.bluesking.api.manager.dao.impl.ProjectDaoImpl;
import cn.bluesking.api.manager.util.MathUtil;

public class TestProjectDao {

    private static final ProjectDao projectDao = new ProjectDaoImpl();
    
    @Before
    public void testSaveProject() {
        Project project = new Project("test" + MathUtil.getRandom(1, 1000), null);
        project = projectDao.saveProject(project);
        System.out.println("【存储项目对象】" + project.toString());
    }
    
    @Test
    public void testGetProject() {
        List<Project> projectList = projectDao.listProject();
        for (Project project : projectList) {
            System.out.println("【查询项目对象】" + project.toString());
        }
    }
    
    @Test
    public void testUpdateProject() {
        // 查询所有
        List<Project> projectList = projectDao.listProject();
        for (Project project : projectList) {
            if (project.getProjectName().contains("test")) {
                project.setProjectName(project.getProjectName().replace("test", "updateTest"));
                project = projectDao.updateProject(project);
                System.out.println("【更新项目对象】" + project.toString());
            }
        }
    }
    
    @After
    public void testDeleteProject() {
        // 查询所有
        List<Project> projectList = projectDao.listProject();
        for (Project project : projectList) {
            if (project.getProjectName().contains("test") || 
                    project.getProjectName().contains("Test")) {
                projectDao.deleteProject(project.getPid());
                System.out.println("【删除项目对象】" + project.toString());
            }
        }
    }
    
}
