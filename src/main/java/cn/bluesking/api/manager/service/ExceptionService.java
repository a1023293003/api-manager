package cn.bluesking.api.manager.service;

import javax.servlet.http.HttpServletResponse;

import cn.bluesking.api.manager.annotation.Service;
import cn.bluesking.api.manager.annotation.RequestMapping;
import cn.bluesking.api.manager.constant.RequestMethodConstant;
import cn.bluesking.api.manager.helper.ConfigHelper;
import cn.bluesking.api.manager.helper.ServletHelper;
import cn.bluesking.api.manager.util.RegexUtil;

/**
 * 异常处理服务
 * 
 * @author 随心
 *
 */
@Service
public class ExceptionService {

    @RequestMapping(path = "/error/404", method = RequestMethodConstant.GET)
    public static void notFound() {
        String message = ConfigHelper.getNotFoundMessage(); 
        String contentType = ConfigHelper.getNotFoundContentType();
        printToResponse(message, contentType);
    }
    
    @RequestMapping(path = "/error/403", method = RequestMethodConstant.GET)
    public static void forbidden() {
        String message = ConfigHelper.getForbiddenMessage();
        String contentType = ConfigHelper.getForbiddenContentType();
        printToResponse(message, contentType);
    }
    
    @RequestMapping(path = "/error/500", method = RequestMethodConstant.GET)
    public static void serviceError() {
        String message = ConfigHelper.getServerErrorMessage();
        String contentType = ConfigHelper.getServerErrorContentType();
        printToResponse(message, contentType);
    }
    
    /**
     * 把提示信息打印到响应流中
     * 
     * @param message     [String]响应提示信息
     * @param contentType [String]响应内容类型
     */
    private static void printToResponse(String message, String contentType) {
        HttpServletResponse response = ServletHelper.getResponse();
        String charset = RegexUtil.regexAString(contentType, "charset *?= *?([\\s\\S]+)");
        response.setCharacterEncoding(charset);
        response.setContentType(contentType);
        ServletHelper.printToResponse(message);
    }
    
}
