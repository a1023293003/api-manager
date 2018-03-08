package cn.bluesking.api.manager.helper;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet助手类
 * 
 * @author 随心
 *
 */
public final class ServletHelper {

    /** slf4j日志配置 */
    private static final Logger _LOG = LoggerFactory.getLogger(ServletHelper.class);

    /** 使每个线程独自拥有一个ServletHelper实例 */
    private static final ThreadLocal<ServletHelper> SERVLET_HOLDER = new ThreadLocal<ServletHelper>();
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    /**
     * 私有构造,单例化ServletHelper
     * 
     * @param request
     * @param response
     */
    private ServletHelper(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    /**
     * 初始化
     * 
     * @param request  [HttpServletRequest]请求对象
     * @param response [HttpServletResponse]响应对象
     */
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        SERVLET_HOLDER.set(new ServletHelper(request, response));
    }
    
    /**
     * 销毁
     */
    public static void destroy() {
        SERVLET_HOLDER.remove();
    }
    
    /**
     * 获取Request对象
     * 
     * @return
     */
    public static HttpServletRequest getRequest() {
        return SERVLET_HOLDER.get().request;
    }
    
    /**
     * 获取Response对象
     * 
     * @return
     */
    public static HttpServletResponse getResponse() {
        return SERVLET_HOLDER.get().response;
    }
    
    /**
     * 获取Session对象
     * 
     * @return
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }
    
    /**
     * 获取ServletContext对象
     * 
     * @return
     */
    public static ServletContext getServletContext() {
        return getRequest().getServletContext();
    }
    
    /**
     * 将属性放入Request中
     * 
     * @param key   [String]索引
     * @param value [Object]值
     */
    public static void setRequestAttribute(String key, Object value) {
        getRequest().setAttribute(key, value);
    }
    
    /**
     * 从Request中获取属性
     * 
     * @param key [String]索引
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(String key){
        return (T) getRequest().getAttribute(key);
    }
    
    /**
     * 从Request中移除属性
     * 
     * @param key [String]索引
     */
    public static void removeRequestAttribute(String key) {
        getRequest().removeAttribute(key);
    }
    
    /**
     * 发送重定向响应
     * 
     * @param location [String]重定向路径
     */
    public static void sendRedirect(String location) {
        try {
            getResponse().sendRedirect(getRequest().getContextPath() + location);
        } catch (Exception e) {
            _LOG.error("重定向失败！", e);
        }
    }
    
    /**
     * 发送转发响应
     * 
     * @param location [String]转发路径
     */
    public static void forward(String location) {
        try {
            getRequest().getRequestDispatcher(location).forward(getRequest(), getResponse());
        } catch (Exception e) {
            _LOG.error("跳转失败！", e);
        }
    }
    
    /**
     * 将属性放入Request中
     * 
     * @param key   [String]索引
     * @param value [Object]值
     */
    public static void setSessionAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }
    
    /**
     * 从Request中获取属性
     * 
     * @param key [String]索引
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key){
        return (T) getSession().getAttribute(key);
    }
    
    /**
     * 从Request中移除属性
     * 
     * @param key [String]索引
     */
    public static void removeSessionAttribute(String key) {
        getSession().removeAttribute(key);
    }
    
    /**
     * 打印内容到当前页面
     * 
     * @param str [String]输出到页面的内容
     */
    public static void printToResponse(String str) {
        try {
            getResponse().getWriter().println(str);
        } catch (IOException e) {
            _LOG.error("输出内容到浏览器失败！", e);
        }
    }
    
    /**
     * 设置响应内容的类型及响应内容的字符编码
     * 
     * @param ContentType [String]响应内容类型
     * @param charset     [String]响应内容编码
     */
    public static void setResponseContentType(String ContentType, String charset) {
        getResponse().setContentType(ContentType);
        getResponse().setCharacterEncoding(charset);
    }
    
}
