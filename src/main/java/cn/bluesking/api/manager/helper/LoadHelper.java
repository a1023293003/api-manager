package cn.bluesking.api.manager.helper;

import cn.bluesking.api.manager.core.generator.DataGenerator;
import cn.bluesking.api.manager.util.ClassUtil;

/**
 * 加载相应的类
 * 
 * @author 随心
 *
 */
public class LoadHelper {

	/**
	 * 加载相应的类
	 */
	public static void init() {
		Class<?>[] classList = {
		        ClassHelper.class,
		        DataSourceHelper.class,
		        DataGenerator.class,
		        ConfigHelper.class
		};
		for(Class<?> clazz : classList) {
			ClassUtil.loadClass(clazz.getName());
		}
	}
}
