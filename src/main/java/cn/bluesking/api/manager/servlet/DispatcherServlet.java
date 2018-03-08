package cn.bluesking.api.manager.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bluesking.api.manager.bean.Api;
import cn.bluesking.api.manager.bean.ApiFileParam;
import cn.bluesking.api.manager.bean.ApiFormParam;
import cn.bluesking.api.manager.bean.ApiParam;
import cn.bluesking.api.manager.bean.Request;
import cn.bluesking.api.manager.bean.RequestParam;
import cn.bluesking.api.manager.constant.RequestMethodConstant;
import cn.bluesking.api.manager.core.generator.DataGenerator;
import cn.bluesking.api.manager.dao.ApiDao;
import cn.bluesking.api.manager.dao.impl.ApiDaoImpl;
import cn.bluesking.api.manager.helper.LoadHelper;
import cn.bluesking.api.manager.helper.RequestHelper;
import cn.bluesking.api.manager.helper.ServiceHelper;
import cn.bluesking.api.manager.helper.ServletHelper;
import cn.bluesking.api.manager.helper.UploadHelper;
import cn.bluesking.api.manager.util.CollectionUtil;

/**
 * 请求转发器
 * 
 * @author 随心
 *
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    /** 序列化id */
    private static final long serialVersionUID = 2907209402773982341L;

    /** slf4j日志配置 */
    private static final Logger _LOG = LoggerFactory.getLogger(DispatcherServlet.class);

    /** api信息数据存取对象 */
    private ApiDao apiDao;
    
    /**
     * 初始化servlet
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        _LOG.info("初始化DispatcherServlet");
        LoadHelper.init();
        apiDao = new ApiDaoImpl();
        // 注册静态资源处理的Servlet
        config.getServletContext().getServletRegistration("jsp").addMapping("/view/*");
        config.getServletContext().getServletRegistration("default").addMapping("/asset/*");
    }

    /**
     * 校验请求参数,判断当前请求是否具备正常访问api接口所必须的参数
     * 
     * @param requestParam [RequestParam]请求参数封装对象
     * @param apiParam     [ApiParam]api参数封装对象
     * @return [boolean]api参数中必须的参数都能在请求参数中找到的时候方法返回true,否则方法返回false
     */
    private boolean checkParameters(RequestParam requestParam, ApiParam apiParam) {
        // api需求参数为空
        if (apiParam == null) {
            return true;
        } else {
            List<ApiFormParam> apiFormParamList = apiParam.getFormParamList();
            if (CollectionUtil.isNotEmpty(apiFormParamList)) {
                // 校验表单参数
                for (ApiFormParam apiFormParam : apiFormParamList) {
                    if (apiFormParam.isNecessary()
                            && !requestParam.containsField(apiFormParam.getFieldName())) {
                        return false;
                    }
                }
            }
            List<ApiFileParam> apiFileParamList = apiParam.getFileParamList();
            if (CollectionUtil.isNotEmpty(apiFileParamList)) {
                // 校验文件参数
                for (ApiFileParam apiFileParam : apiFileParamList) {
                    if (apiFileParam.isNecessary()
                            && !requestParam.containsField(apiFileParam.getFieldName())) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
    
    /**
     * 获取请求参数
     * 
     * @param request [HttpServletRequest]http请求对象
     * @return [RequestParam]请求参数
     * @throws IOException io操作出错时抛出该异常
     */
    private RequestParam getRequestParam(HttpServletRequest request) throws IOException {
        // 获取请求参数
        if(UploadHelper.isMultiPart(request)) {
            // 文件上传
            return UploadHelper.createParam(request);
        } else {
            // 普通表单
            return RequestHelper.createParam(request);
        }
    }
    
    @Override
    protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        ServletHelper.init(httpRequest, httpResponse);
        try {
            _LOG.debug("uri : " + httpRequest.getRequestURI());
            _LOG.debug("url : " + httpRequest.getRequestURL());
            _LOG.debug("method : " + httpRequest.getMethod());
            _LOG.debug("context-path : " + httpRequest.getServletContext().getContextPath());
            // 获取请求对象
            Request request = getRequest(httpRequest);
            _LOG.debug("request : " + request.toString());
            
            if (ServiceHelper.containsService(request)) {
                _LOG.debug("该请求有实现的方法");
                // 获取请求参数
                RequestParam requestParam = getRequestParam(httpRequest);
                // 当前请求有对应的功能实现
                ServiceHelper.invokeServiceMethod(request, requestParam);
            } else {
                // 通过请求获取api信息
                Api api = apiDao.getApi(request);
                if (api != null) {
                    _LOG.debug("请求有对应的api对象");
                    // 获取请求参数
                    RequestParam requestParam = getRequestParam(httpRequest);
                    // 校验api必须参数本次请求是否具有
                    if (checkParameters(requestParam, api.getParam())) {
                        // 正常请求
                        requestHanding(api, httpResponse);
                        
                    } else {
                        // 403
                        ServiceHelper.invokeServiceMethod("/error/403", RequestMethodConstant.GET);
                    }
                } else {
                    _LOG.debug("请求404");
                    // 404
                    ServiceHelper.invokeServiceMethod("/error/404", RequestMethodConstant.GET);
                }
            }
            
        } catch (Exception e) {
            // 500
            ServiceHelper.invokeServiceMethod("/error/500", RequestMethodConstant.GET);
        } finally {
            ServletHelper.destroy();
        }
        
    }

    /**
     * 获取本次请求信息对象
     * 
     * @param request [HttpServletRequest]http请求对象
     * @return [Request]方法执行成功将返回本次请求信息对象
     */
    private Request getRequest(HttpServletRequest request) {
        // 通过请求方法和滤掉contextPath的uri来获取本次请求的请求对象
        String contextPath = request.getServletContext().getContextPath();
        String method = request.getMethod();
        // uri过滤掉contextPath
        String uri = request.getRequestURI();
        if (uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        } else {
            _LOG.error("请求url不合法!");
        }
        return new Request(uri, method);
    }
    
    /**
     * 请求处理
     * 
     * @param api      [Api]接口对象
     * @param response [HttpServletResponse]http请求响应对象
     */
    private void requestHanding(Api api, HttpServletResponse response) {
        response.setContentType(api.getContentType());
        response.setCharacterEncoding(api.getEncoding());
        try {
            System.err.println("[" + api.decodeAndGetTemplate() + "]");
            response.getWriter().print(DataGenerator.generate(api.decodeAndGetTemplate(), 1));
        } catch (IOException e) {
            _LOG.error("响应请求内容失败!" + e.getMessage());
        }
    }
    
}
