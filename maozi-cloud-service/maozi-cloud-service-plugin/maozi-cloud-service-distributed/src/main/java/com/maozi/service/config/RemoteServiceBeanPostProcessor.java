package com.maozi.service.config;

import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.service.api.annotation.RemoteService;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link RemoteService} 服务暴露处理器（微服务模式），分两阶段工作：
 * <p>
 * <b>阶段一：注册为 Spring Bean</b>（{@link BeanDefinitionRegistryPostProcessor}）。
 * {@link RemoteService} 是裸标记注解，不像 {@code @DubboService} 那样能被 Dubbo 的
 * {@code ServiceClassPostProcessor} 自动注册为 bean；而它也不能直接加 {@code @Component}
 * 元注解——因为典型用法是 {@code RpcXxxServiceImpl extends XxxServiceImpl implements RpcXxxService}，
 * 父类 {@code XxxServiceImpl} 已是 {@code @Service} bean，子类若也成为 bean 会与父类在
 * {@code XxxService} 类型上产生自动装配歧义。因此本处理器在注册时将 bean 标记为
 * {@code autowireCandidate = false}，使其<b>能被创建</b>（依赖照常注入）但<b>不参与按类型注入</b>。
 * </p>
 * <p>
 * <b>阶段二：暴露为 Dubbo 服务</b>（{@link SmartInitializingSingleton}）。
 * 在所有非懒加载单例 bean 创建完毕（含 AOP 代理生成）之后，遍历容器中标注 {@link RemoteService}
 * 的 bean，将其暴露为 Dubbo 远程服务。之所以不在 {@code BeanPostProcessor} 阶段导出，是因为
 * BPP 的 {@code postProcessAfterInitialization} 执行顺序不保证在 AOP 代理创建之后——若先于
 * {@code AbstractAutoProxyCreator} 执行，{@code service.setRef(bean)} 拿到的是未代理的原始对象，
 * Dubbo 调用时切面（如日志、事务）不会生效。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/25
 */
@Component
public class RemoteServiceBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, SmartInitializingSingleton, ApplicationContextAware {

	/** 已暴露过的实现类型，避免重复导出 */
	private final Set<Class<?>> exportedClasses = ConcurrentHashMap.newKeySet();

	/** Spring 应用上下文，用于在单例初始化完成后按注解查找 bean */
	private ApplicationContext applicationContext;

	// ==================== 阶段一：注册 @RemoteService 类为 Spring Bean ====================

	@Override
	public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RemoteService.class));
		for (BeanDefinition candidate : scanner.findCandidateComponents(ApplicationEnvironmentContext.PACKAGE_PREFIX)) {
			// 不参与按类型自动装配，避免与父类 @Service（如 ClientServiceImpl）在共同接口上产生歧义
			candidate.setAutowireCandidate(false);
			BeanDefinitionReaderUtils.registerWithGeneratedName((AbstractBeanDefinition) candidate, registry);
		}
	}

	@Override
	public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 无需处理
	}

	// ==================== 阶段二：暴露 @RemoteService Bean 为 Dubbo 服务 ====================

	@Override
	public void afterSingletonsInstantiated() {
		Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(RemoteService.class);
		for (Object bean : serviceBeans.values()) {
			Class<?> beanClass = ClassUtils.getUserClass(bean);
			if (exportedClasses.add(beanClass)) {
				exportService(bean, beanClass);
			}
		}
	}

	@Override
	public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 将实现类暴露为 Dubbo 远程服务。
	 * <p>
	 * 此时的 {@code bean} 已经是 AOP 代理后的最终实例，Dubbo 调用方法时会经过所有切面。
	 * </p>
	 *
	 * @param bean      Spring 容器中的 bean 实例（已含 AOP 代理），作为服务实现引用
	 * @param beanClass 去除 CGLIB 增强后的真实实现类
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void exportService(Object bean, Class<?> beanClass) {
		Class<?> interfaceClass = resolveServiceInterface(beanClass);
		ServiceConfig service = new ServiceConfig();
		service.setInterface(interfaceClass);
		service.setRef(bean);
		DubboBootstrap.getInstance().service(service);
		service.export();
	}

	/**
	 * 从实现类的直接声明接口中解析要暴露的 RPC 接口。
	 * <p>
	 * 取 {@link Class#getInterfaces()} 返回的直接声明接口（不含父类继承的接口），
	 * 过滤掉 {@code java.*} 包下的标记接口（如 {@code Serializable}、{@code Cloneable}），
	 * 取第一个剩余接口作为 Dubbo 服务接口。
	 * </p>
	 *
	 * @param beanClass 真实实现类
	 * @return 要暴露的 RPC 接口
	 * @throws IllegalStateException 实现类没有声明任何非 {@code java.*} 接口时抛出
	 */
	private Class<?> resolveServiceInterface(Class<?> beanClass) {
		for (Class<?> iface : beanClass.getInterfaces()) {
			if (!iface.getName().startsWith("java.")) {
				return iface;
			}
		}
		throw new IllegalStateException("被 @RemoteService 标注的类 " + beanClass.getName()
			+ " 未直接实现任何业务接口，无法暴露为 Dubbo 服务");
	}

}
