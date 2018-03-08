package cn.bluesking.api.manager.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.Project;
import cn.bluesking.api.manager.bean.Request;
import cn.bluesking.api.manager.util.CollectionUtil;
import cn.bluesking.api.manager.util.JsonUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * 数据源助手类
 * 
 * @author 随心
 *
 */
public final class DataSourceHelper {
    
    /** slf4j日志配置 */
    private static final Logger _LOG = LoggerFactory.getLogger(DataSourceHelper.class);

    /** 项目名和Project对象组成的映射 */
    private static final Map<String, Project> PROJECT_MAP;
    
    /** pid和Project对象组成的映射 */
    private static final Map<Integer, Project> PID_PROJECT_MAP;
    
    /** 请求对象和api对象组成的映射 */
    private static final Map<Request, Api> API_MAP;
    
    /** aid和api对象组成的映射 */
    private static final Map<Integer, Api> AID_API_MAP;
    
    /** 当前最新的pid */
    private static AtomicInteger LAST_PID;
    
    /** 当前最新的aid */
    private static AtomicInteger LAST_AID;
    
    static {
        Map<String, String> dataSource = ConfigHelper.getApiConfigMap();
        PROJECT_MAP = new HashMap<String, Project>(dataSource.size());
        PID_PROJECT_MAP = new HashMap<Integer, Project>(dataSource.size());
        API_MAP = new HashMap<Request, Api>();
        AID_API_MAP = new HashMap<Integer, Api>();
        
        // 从配置文件中读取到项目信息和api信息
        Project project;
        List<Api> apiList;
        int lastPid = 1;
        int lastAid = 1;
        for (Entry<String, String> entry : dataSource.entrySet()) {
            project = JsonUtil.fromJson(entry.getValue(), Project.class);
            // 获取最新pid
            lastPid = project.getPid() >= lastPid ? project.getPid() + 1 : lastPid;
            PROJECT_MAP.put(entry.getKey(), project);
            PID_PROJECT_MAP.put(project.getPid(), project);
            
            apiList = project.getApiList();
            for (Api api : apiList) {
                // 获取最新aid
                lastAid = api.getAid() >= lastAid ? api.getAid() + 1 : lastAid;
                API_MAP.put(new Request(api.getRequestPath(), api.getRequestMethod()), api);
                AID_API_MAP.put(api.getAid(), api);
            }
        }
        // 创建最新的pid和aid原子整型
        LAST_PID = new AtomicInteger(lastPid);
        LAST_AID = new AtomicInteger(lastAid);
    }
    
    /**
     * 获取所有项目信息
     * 
     * @return [List<Project>]该方法执行成功后会返回所有项目对象
     */
    public static List<Project> listProject() {
        List<Project> projectList = new ArrayList<Project>(PROJECT_MAP.size());
        for (Entry<String, Project> entry : PROJECT_MAP.entrySet()) {
            projectList.add(entry.getValue().cloneProject());
        }
        return projectList;
    }
    
    /**
     * 通过项目名称获取项目信息
     * 
     * @param projectName [String]项目名称
     * @return [Project]方法执行查询项目操作成功后会返回查询成功的项目信息,执行失败后返回null
     */
    public static Project getProject(String projectName) {
        Project project = PROJECT_MAP.get(projectName);
        return project == null ? null : project.cloneProject();
    }
    
    /**
     * 通过pid(唯一标识id)查询项目信息
     * 
     * @param pid [int]对应一个项目信息的唯一标识符
     * @return [Project]方法执行查询项目操作成功后会返回查询成功的项目信息,执行失败后返回null
     */
    public static Project getProject(int pid) {
        Project project = PID_PROJECT_MAP.get(pid);
        return project == null ? null : project.cloneProject();
    }
    
    /**
     * 添加项目信息
     * 
     * @param project [Project]待添加项目对象
     * @return [Project]方法执行添加项目操作成功会返回添加的项目信息(包含pid),执行操作失败后返回null
     */
    public static synchronized Project saveProject(Project project) {
        String projectName = project.getProjectName();
        if (StringUtil.isEmpty(projectName)) {
            // 项目名称不合法
            throw new IllegalArgumentException("项目名称不能为空:" + projectName + ", 保存项目信息操作执行失败!");
            
        } else if (!PROJECT_MAP.containsKey(projectName) 
                && (project.getPid() == null || !PID_PROJECT_MAP.containsKey(project.getPid()))) {
            // 生成并设置pid
            project.setPid(LAST_PID.getAndIncrement());
            // 保存项目映射,存储的是项目对象的克隆对象
            Project copyProject = project.cloneProject();
            // 清空映射项目中所有api信息
            copyProject.getApiList().clear();
            PROJECT_MAP.put(projectName, copyProject);
            PID_PROJECT_MAP.put(project.getPid(), copyProject);
            // 保存api
            Api api;
            List<Api> apiList = project.getApiList();
            for (int i = 0; i < apiList.size(); i ++) {
                api = apiList.get(i);
                api.setPid(project.getPid());
                if (saveApi(api) == null) {
                    apiList.remove(i);
                    i --;
                }
            }
            // 更新项目信息到配置剖析器
            ConfigHelper.putApiConfig(projectName, JsonUtil.toJson(copyProject), null);
            return project;
            
        } else {
            // 更新项目
            return updateProject(project);
        }
    }
    
    /**
     * 更新项目对象,传入数据之前已经保证pid和oldProjectName的有效性了
     * <pre>
     * 更新项目名和项目映射
     * 更新pid和项目映射
     * 更新配置解析器内存中的配置数据
     * </pre>
     * 
     * @param project        [Project]更新后的项目对象
     * @param pid            [iInteger]pid项目对象唯一标识符
     * @param oldProjectName [String]原项目名称
     * @return [Project]方法执行成功会返回一个修改后项目内容相同的项目对象,方法执行失败返回null
     */
    private static synchronized Project updateProject(Project project, String oldProjectName) {
        // 通过pid从映射中取出来的项目对象
        Project pidProject = PID_PROJECT_MAP.get(project.getPid());
        String newProjectName = project.getProjectName();
        
        if (!StringUtil.equals(oldProjectName, newProjectName)) {
            // 如果修改过项目名称,则取修改项目映射
            pidProject.setProjectName(newProjectName);
            PROJECT_MAP.remove(oldProjectName);
            PROJECT_MAP.put(newProjectName, pidProject);
        }
        
        // 删除传入项目对象中不合法的api信息
        Api api;
        List<Api> apiList = project.getApiList();
        for (int i = 0; i < apiList.size(); i ++) {
            api = apiList.get(i);
            // pid校验,pid不合法的api信息都删除掉
            if (api.getPid() != project.getPid()) {
                apiList.remove(i);
                i --;
            }
        }
        // 删除映射中项目对象更新后不存在的api信息
        Api pidApi;
        boolean contains;
        List<Api> pidApiList = pidProject.getApiList();
        List<Api> waitForsaveApiList = new ArrayList<Api>(apiList);
        
        for (int i = 0; i < pidApiList.size(); i ++) {
            pidApi = pidApiList.get(i);
            contains = false;
            
            for (int k = 0; k < waitForsaveApiList.size(); k ++) {
                api = waitForsaveApiList.get(k);
                if (api.getAid() == pidApi.getAid()) {
                    // api存在
                    contains = true;
                    // 更新
                    updateApi(waitForsaveApiList.get(k));
                    // 删除已更新的api信息
                    waitForsaveApiList.remove(k);
                    break;
                }
            }
            
            if (!contains) {
                // 不包含的api信息要删掉
                pidApiList.remove(i);
                API_MAP.remove(new Request(pidApi.getRequestPath(), pidApi.getRequestMethod()));
                AID_API_MAP.remove(pidApi.getAid());
                i --;
            }
        }
        // 把传入项目对象中合法的api添加到映射项目对象中
        for (int i = 0; i < waitForsaveApiList.size(); i ++) {
            saveApi(waitForsaveApiList.get(i));
        }
        
        // 更新配置剖析器内存中的配置键值对
        ConfigHelper.updateApiConfig(oldProjectName, newProjectName, JsonUtil.toJson(pidProject));
        return project;
    }
    
    /**
     * 更新项目信息
     * 
     * @param project [Project]更新后的项目对象
     * @return [Project]方法执行更新项目操作成功后会返回更新后的项目信息,执行失败后返回null
     */
    public static synchronized Project updateProject(Project project) {
        boolean existPid = PID_PROJECT_MAP.containsKey(project.getPid());
        boolean existProjectName = PROJECT_MAP.containsKey(project.getProjectName());
        
        if (existPid && existProjectName) {
            // 存在pid也存在项目名
            Project pidProject = PID_PROJECT_MAP.get(project.getPid());
            Project nameProject = PROJECT_MAP.get(project.getProjectName());
            
            if (pidProject != nameProject) {
                // 两个索引找到的Project对象不统一,不知道如何修改
                return null;
            } else {
                return updateProject(project, project.getProjectName());
            }
            
        } else if (existPid) {
            // 存在pid但不存在项目名
            Project pidProject = PID_PROJECT_MAP.get(project.getPid());
            return updateProject(project, pidProject.getProjectName());
            
        } else {
            return null;
        }
        
    }
    
    /**
     * 通过pid(唯一标识id)删除项目信息
     * 
     * @param pid [int]对应一个项目信息的唯一标识符
     * @return [Project]方法执行删除项目操作成功后会返回删除成功的项目信息,执行失败后返回null
     */
    public static synchronized Project deleteProject(int pid) {
        if (!PID_PROJECT_MAP.containsKey(pid)) {
            return null;
        } else {
            Project project = PID_PROJECT_MAP.get(pid);
            PID_PROJECT_MAP.remove(pid);
            PROJECT_MAP.remove(project.getProjectName());
            List<Api> apiList = project.getApiList();
            for (Api api : apiList) {
                API_MAP.remove(new Request(api.getRequestPath(), api.getRequestMethod()));
                AID_API_MAP.remove(api.getAid());
            }
            // 删除配置剖析器内存中的配置键值对
            ConfigHelper.removeApiConfig(project.getProjectName());
            return project;
        }
    }
    
    /**
     * 获取所有api信息
     * 
     * @return [List<Api>]该方法执行成功后会返回所有api对象
     */
    public static List<Api> listApi() {
        List<Api> apiList = new ArrayList<Api>(API_MAP.size());
        for (Entry<Request, Api> entry : API_MAP.entrySet()) {
            apiList.add(entry.getValue().cloneApi());
        }
        return apiList;
    }
    
    /**
     * 通过请求对象获取api信息
     * 
     * @param request [Request]请求对象
     * @return [Api]api信息
     */
    public static Api getApi(Request request) {
        Api api = API_MAP.get(request);
        return api == null ? null : api.cloneApi();
    }
    
    /**
     * 通过aid(唯一标识id)查询api信息
     * 
     * @param aid [int]对应一个api信息的唯一标识符
     * @return [Api]方法执行查询api操作成功后会返回查询成功的api信息,执行失败后返回null
     */
    public static Api getApi(int aid) {
        Api api = AID_API_MAP.get(aid);
        return api == null ? null : api.cloneApi();
    }
    
    /**
     * 添加api信息
     * 
     * @param api [Api]待添加api对象
     * @return [Api]方法执行添加api操作成功会返回添加的api信息(包含uid),执行操作失败后返回null
     */
    public static synchronized Api saveApi(Api api) {
        // 仅仅校验pid就好了
        if (!PID_PROJECT_MAP.containsKey(api.getPid())) {
            // 项目不存在,直接返回null
            _LOG.debug("存储api失败!api所属的项目不存在!");
            return null;
            
        } else {
            Request request = new Request(api.getRequestPath(), api.getRequestMethod());
            boolean existRequest = API_MAP.containsKey(request);
            boolean existAid = AID_API_MAP.containsKey(api.getAid());
            
            if (!existRequest && !existAid) {
                // 初次添加该api对象
                api.setAid(LAST_AID.getAndIncrement());
                // 添加的是克隆的api对象
                Api copyApi = api.cloneApi();
                API_MAP.put(request, copyApi);
                AID_API_MAP.put(copyApi.getAid(), copyApi);
                
                // 添加api到项目中去
                Project project = PID_PROJECT_MAP.get(api.getPid());
                project.getApiList().add(copyApi);
                
                // 更新项目信息到配置剖析器
                ConfigHelper.updateApiConfig(project.getProjectName(), JsonUtil.toJson(project));
                return api;
                
            } else {
                // 如果已存在api对象,进行更新操作
                return updateApi(api);
            }
        }
    }
    
    /**
     * 修改api信息,方法默认传入的aid和pid是均是有效的
     * <pre>
     * 修改请求和api对象的映射
     * 修改项目中的aid
     * 修改配置解剖器内存中的配置键值对
     * </pre>
     * 
     * @param api [Api]修改后的api对象
     * @return [Api]方法执行成功后会返回一个和修改后api信息内容相同的api对象,方法执行失败返回null
     */
    private static synchronized Api updateApi0(Api api) {
        // 通过aid获取的api对象
        Api aidApi = AID_API_MAP.get(api.getAid());
        if (!StringUtil.equals(api.getRequestMethod(), aidApi.getRequestMethod()) || 
                !StringUtil.equals(api.getRequestPath(), aidApi.getRequestPath())) {
            API_MAP.remove(new Request(aidApi.getRequestPath(), aidApi.getRequestMethod()));
            aidApi.setRequestMethod(api.getRequestMethod());
            aidApi.setRequestPath(api.getRequestPath());
            API_MAP.put(new Request(aidApi.getRequestPath(), aidApi.getRequestMethod()), aidApi);
        }
        aidApi.setContentType(api.getContentType());
        aidApi.setEncoding(api.getEncoding());
        if (api.getParam() != null) {
            aidApi.setParam(api.getParam().cloneApiParam());
        } else {
            aidApi.setParam(null);
        }
        aidApi.setResponseContentConfiguration(api.getResponseContentConfiguration());
        
        Project project = PID_PROJECT_MAP.get(api.getPid());
        ConfigHelper.updateApiConfig(project.getProjectName(), JsonUtil.toJson(project));
        return api;
    }
    
    /**
     * 更新api信息
     * 
     * @param api [Api]更新后的api对象
     * @return [Api]方法执行更新api操作成功后会返回更新后的api信息,执行失败后返回null
     */
    public static synchronized Api updateApi(Api api) {
        if (!PID_PROJECT_MAP.containsKey(api.getPid())) {
            // 所属项目不存在
            _LOG.debug("更新api失败!api所属项目不存在!");
            return null;
            
        } else {
            // 所属项目是存在的
            Request request = new Request(api.getRequestPath(), api.getRequestMethod());
            boolean existRequest = API_MAP.containsKey(request);
            boolean existAid = AID_API_MAP.containsKey(api.getAid());
            
            if (existRequest && existAid) {
                // 存在aid也存在request
                Api requestApi = API_MAP.get(request);
                Api aidApi = AID_API_MAP.get(api.getAid());
                
                if (requestApi != aidApi) {
                    // 两个索引找到的api对象不统一,不知道如何修改
                    _LOG.debug("更新api失败!两个索引找到的api对象不统一,不知道如何修改!");
                    return null;
                    
                } else {
                    return updateApi0(api);
                }
                
            } else if (existAid) {
                // 存在aid但不存在request
                return updateApi0(api);
                
            } else {
                _LOG.debug("更新api失败!api的aid不存在!");
                return null;
            }
        }
    }
    
    /**
     * 通过uid(唯一标识id)删除api信息
     * 
     * @param aid [int]对应一个api信息的唯一标识符
     * @return [Api]方法执行删除api操作成功后会返回删除成功的api信息,执行失败后返回null
     */
    public static synchronized Api deleteApi(int aid) {
        if (!AID_API_MAP.containsKey(aid)) {
            return null;
            
        } else {
            Api api = AID_API_MAP.get(aid);
            // 删除项目中的api信息
            Project project = PID_PROJECT_MAP.get(api.getPid());
            List<Api> apiList = project.getApiList();
            if (CollectionUtil.isNotEmpty(apiList)) apiList.remove(api);
            
            // 删除api映射
            API_MAP.remove(new Request(api.getRequestPath(), api.getRequestMethod()));
            AID_API_MAP.remove(aid);
            
            // 更新项目信息到配置剖析器
            ConfigHelper.updateApiConfig(project.getProjectName(), JsonUtil.toJson(project));
            return api;
        }
    }
    
    /**
     * 更新api内容到配置文件
     */
    public static void updateToProperties() {
        ConfigHelper.updateApiConfig();
    }
    
}
