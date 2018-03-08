package cn.bluesking.api.manager.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.bluesking.api.manager.bean.RequestFormParam;
import cn.bluesking.api.manager.bean.RequestParam;
import cn.bluesking.api.manager.util.ArrayUtil;
import cn.bluesking.api.manager.util.CodecUtil;
import cn.bluesking.api.manager.util.StreamUtil;
import cn.bluesking.api.manager.util.StringUtil;

/**
 * 请求助手类
 * 
 * @author 随心
 *
 */
public final class RequestHelper {

    /**
     * 创建请求参数对象
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static RequestParam createParam(HttpServletRequest request) throws IOException {
        List<RequestFormParam> formParamList = new ArrayList<RequestFormParam>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));
        return new RequestParam(formParamList);
    }

    /**
     * 获取Parameter中的参数
     * 
     * @param request [HttpServletRequest]http请求封装对象
     * @return [List<RequestFormParam>]该方法将返回通过getParameter方法获取的http请求参数集合
     */
    private static List<RequestFormParam> parseParameterNames(HttpServletRequest request) {
        List<RequestFormParam> formParamList = new ArrayList<RequestFormParam>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)) {
                Object fieldValue;
                if (fieldValues.length == 1) {
                    // 同一个参数值一个参数名
                    fieldValue = fieldValues[0];
                } else {
                    // 同一参数名多个参数值
                    StringBuilder sBuilder = new StringBuilder(fieldValues[0]);
                    for (int i = 1; i < fieldValues.length; i++) {
                        sBuilder.append(StringUtil.SEPARATOR + fieldValues[i]);
                    }
                    fieldValue = sBuilder.toString();
                }
                formParamList.add(new RequestFormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    /**
     * 获取InputStream中的参数,主要针对的是text/plain格式传输的数据
     * 
     * @param request [HttpServletRequest]http请求封装对象
     * @return [List<RequestFormParam>]该方法将返回通过getInputStream方法获取的http请求参数集合
     * @throws IOException
     */
    private static List<RequestFormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<RequestFormParam> formParamList = new ArrayList<RequestFormParam>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] keyValues = body.split("&");
            if (ArrayUtil.isNotEmpty(keyValues)) {
                for (String keyValue : keyValues) {
                    String[] array = keyValue.split("=");
                    if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        String fieldName = array[0];
                        String fieldValue = array[1];
                        formParamList.add(new RequestFormParam(fieldName, fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }
}
