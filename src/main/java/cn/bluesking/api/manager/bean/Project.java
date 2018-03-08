package cn.bluesking.api.manager.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目对象
 * 
 * @author 随心
 *
 */
public class Project implements Cloneable {

    /** 项目唯一标识的id */
    private Integer pid;
    
    /** 项目名称 */
    private String projectName;
    
    /** 项目中的api信息 */
    private List<Api> apiList;

    public Project() {
        apiList = new ArrayList<Api>(2);
    }
    
    public Project(int pid, String projectName, List<Api> apiList) {
        this.pid = pid;
        this.projectName = projectName;
        this.apiList = apiList == null ? new ArrayList<Api>(2) : apiList;
    }
    
    public Project(String projectName, List<Api> apiList) {
        this.projectName = projectName;
        this.apiList = apiList == null ? new ArrayList<Api>(2) : apiList;
    }

    public Integer getPid() {
        return pid;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public List<Api> getApiList() {
        return apiList;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setApiList(List<Api> apiList) {
        this.apiList = apiList;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Project project = (Project) super.clone();
        List<Api> copyApiList = null;
        if (apiList != null) {
            copyApiList = new ArrayList<Api>(apiList.size());
            for (Api api : apiList) {
                copyApiList.add((Api) api.clone());
            }
        }
        project.setApiList(copyApiList);
        return project;
    }
    
    public Project cloneProject() {
        try {
            return (Project) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Project [pid=" + pid + ", projectName=" + projectName + ", apiList=" + apiList + "]";
    }
    
}
