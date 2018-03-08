package cn.bluesking.api.manager.core.generator;

import org.junit.Test;

/**
 * JSON数据生成器测试类
 * 
 * @author 随心
 *
 */
public class TestJsonGenerator {

    @Test
    public void testGenerator() {
        String template = "$randomInt(10, 100)";
        System.out.println(DataGenerator.generate(template, 1));
        template = "{\"b\":$randomInt(10, 100)}";
        System.out.println(DataGenerator.generate(template, 3));
    }
}
