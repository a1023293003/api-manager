package cn.bluesking.api.manager.helper;

/**
 * 异常处理助手类
 * 
 * @author 随心
 *
 */
public final class ExceptionHelper {
    
    /**
     * 打印错误信息并退出
     * 
     * @param e [Exception]异常对象
     */
    public static void printAndExit(Exception e) {
        printAndExit("", e);
    }
    
    /**
     * 打印错误信息并退出
     * 
     * @param message [String]错误信息
     * @param e       [Exception]异常对象
     */
    public static void printAndExit(String message, Exception e) {
        if (e != null) {
            System.out.println(message + " " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println(message);
        }
        System.exit(1);
    }
}
