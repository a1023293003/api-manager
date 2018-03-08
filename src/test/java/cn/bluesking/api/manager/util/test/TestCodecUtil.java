package cn.bluesking.api.manager.util.test;

import org.junit.Test;

import cn.bluesking.api.manager.util.CodecUtil;

public class TestCodecUtil {

    @Test
    public void testEncodeHTML() {
        String result = CodecUtil.encodeHTML("abcdefghijklmnopqrstuvwxyz♦∫");
        System.out.println(result);
        System.out.println(CodecUtil.decodeHTML(result));
        result = CodecUtil.encodeHTML("你好呀");
        System.out.println(result);
        System.out.println(CodecUtil.decodeHTML(result));
        System.out.println(CodecUtil.decodeHTML("&diams;&int;&int1;"));
    }
    
    @Test
    public void testDecodeUnicode() {
        String result = CodecUtil.decodeUnicode(
                "\\u0034\\u0030\\u0034\\u9875\\u9762\\u9519\\u8BEF\\u63D0\\u793A");
        System.out.println(result);
    }
    
    @Test
    public void testEncodeUnicode() {
        String result = CodecUtil.encodeUnicode("404页面错误提示");
        System.out.println(result);
        System.out.println(CodecUtil.decodeUnicode(result));
    }
    
}
