package cn.bluesking.api.manager.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * api参数对象
 * 
 * @author 随心
 *
 */
public class ApiParam implements Cloneable {

    /** api封装表单参数集合 */
    private List<ApiFormParam> formParamList;
    
    /** api封装上传文件参数集合 */
    private List<ApiFileParam> fileParamList;
    
    public ApiParam(List<ApiFormParam> formParamList) {
        this.formParamList = formParamList;
    }
    
    public ApiParam(List<ApiFormParam> formParamList, List<ApiFileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    public List<ApiFormParam> getFormParamList() {
        return formParamList;
    }

    public List<ApiFileParam> getFileParamList() {
        return fileParamList;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // 克隆表单参数
        List<ApiFormParam> copyFormParamList = null;
        if (formParamList != null) {
            copyFormParamList = new ArrayList<ApiFormParam>(formParamList.size());
            for (ApiFormParam formParam : formParamList) {
                copyFormParamList.add((ApiFormParam) formParam.clone());
            }
        }
        // 克隆文件参数
        List<ApiFileParam> copyFileParamList = null;
        if (formParamList != null) {
            copyFileParamList = new ArrayList<ApiFileParam>(fileParamList.size());
            for (ApiFileParam fileParam : fileParamList) {
                copyFileParamList.add((ApiFileParam) fileParam.clone());
            }
        }
        return new ApiParam(copyFormParamList, copyFileParamList);
    }
    
    public ApiParam cloneApiParam() {
        try {
            return (ApiParam) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
}
