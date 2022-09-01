package com.jiumao.tool;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;

/**
 * Spring(Spring boot)宸ュ叿灏佽锛屽寘鎷細
 *
 * <pre>
 *     1銆丼pring IOC瀹瑰櫒涓殑bean瀵硅薄鑾峰彇
 * </pre>
 *
 * @author loolly
 * @since 5.1.0
 */
@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringUtil.applicationContext = applicationContext;
	}

	/**
	 * 鑾峰彇applicationContext
	 *
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	//閫氳繃name鑾峰彇 Bean.

	/**
	 * 閫氳繃name鑾峰彇 Bean
	 *
	 * @param <T>  Bean绫诲瀷
	 * @param name Bean鍚嶇О
	 * @return Bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 閫氳繃class鑾峰彇Bean
	 *
	 * @param <T>   Bean绫诲瀷
	 * @param clazz Bean绫�
	 * @return Bean瀵硅薄
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	/**
	 * 閫氳繃name,浠ュ強Clazz杩斿洖鎸囧畾鐨凚ean
	 *
	 * @param <T>   bean绫诲瀷
	 * @param name  Bean鍚嶇О
	 * @param clazz bean绫诲瀷
	 * @return Bean瀵硅薄
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}

	/**
	 * 閫氳繃绫诲瀷鍙傝�冭繑鍥炲甫娉涘瀷鍙傛暟鐨凚ean
	 *
	 * @param reference 绫诲瀷鍙傝�冿紝鐢ㄤ簬鎸佹湁杞崲鍚庣殑娉涘瀷绫诲瀷
	 * @param <T>       Bean绫诲瀷
	 * @return 甯︽硾鍨嬪弬鏁扮殑Bean
	 * @since 5.4.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(TypeReference<T> reference) {
		final ParameterizedType parameterizedType = (ParameterizedType) reference.getType();
		final Class<T> rawType = (Class<T>) parameterizedType.getRawType();
		final Class<?>[] genericTypes = Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(Class[]::new);
		final String[] beanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
		return getBean(beanNames[0], rawType);
	}

	/**
	 * 鑾峰彇鎸囧畾绫诲瀷瀵瑰簲鐨勬墍鏈塀ean锛屽寘鎷瓙绫�
	 *
	 * @param <T>  Bean绫诲瀷
	 * @param type 绫汇�佹帴鍙ｏ紝null琛ㄧず鑾峰彇鎵�鏈塨ean
	 * @return 绫诲瀷瀵瑰簲鐨刡ean锛宬ey鏄痓ean娉ㄥ唽鐨刵ame锛寁alue鏄疊ean
	 * @since 5.3.3
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> type) {
		return applicationContext.getBeansOfType(type);
	}

	/**
	 * 鑾峰彇鎸囧畾绫诲瀷瀵瑰簲鐨凚ean鍚嶇О锛屽寘鎷瓙绫�
	 *
	 * @param type 绫汇�佹帴鍙ｏ紝null琛ㄧず鑾峰彇鎵�鏈塨ean鍚嶇О
	 * @return bean鍚嶇О
	 * @since 5.3.3
	 */
	public static String[] getBeanNamesForType(Class<?> type) {
		return applicationContext.getBeanNamesForType(type);
	}

	/**
	 * 鑾峰彇閰嶇疆鏂囦欢閰嶇疆椤圭殑鍊�
	 *
	 * @param key 閰嶇疆椤筴ey
	 * @return 灞炴�у��
	 * @since 5.3.3
	 */
	public static String getProperty(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

	/**
	 * 鑾峰彇褰撳墠鐨勭幆澧冮厤缃紝鏃犻厤缃繑鍥瀗ull
	 *
	 * @return 褰撳墠鐨勭幆澧冮厤缃�
	 * @since 5.3.3
	 */
	public static String[] getActiveProfiles() {
		return applicationContext.getEnvironment().getActiveProfiles();
	}

	/**
	 * 鑾峰彇褰撳墠鐨勭幆澧冮厤缃紝褰撴湁澶氫釜鐜閰嶇疆鏃讹紝鍙幏鍙栫涓�涓�
	 *
	 * @return 褰撳墠鐨勭幆澧冮厤缃�
	 * @since 5.3.3
	 */
	public static String getActiveProfile() {
		final String[] activeProfiles = getActiveProfiles();
		return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
	}

	/**
	 * 鍔ㄦ�佸悜Spring娉ㄥ唽Bean
	 * <p>
	 * 鐢眥@link org.springframework.beans.factory.BeanFactory} 瀹炵幇锛岄�氳繃宸ュ叿寮�鏀続PI
	 *
	 * @param <T>      Bean绫诲瀷
	 * @param beanName 鍚嶇О
	 * @param bean     bean
	 * @author shadow
	 * @since 5.4.2
	 */
	public static <T> void registerBean(String beanName, T bean) {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		context.getBeanFactory().registerSingleton(beanName, bean);
	}
}




