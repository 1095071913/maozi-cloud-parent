package com.maozi.service.config;

import com.maozi.service.api.annotation.RemoteResource;
import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单体服务 {@code @RemoteResource} 字段本地注入处理器。
 * <p>
 * 单体服务（maozi-cloud-all-service）不引入 maozi-cloud-service-distributed（Dubbo 模块），
 * {@link RemoteResource} 标注的字段不会有 Dubbo 引用代理注入。
 * 注解上的 {@code @Resource} 元注解也无效——Spring 的
 * {@code CommonAnnotationBeanPostProcessor} 使用 {@code field.isAnnotationPresent(Resource.class)}
 * 判断字段，该方法只检查直接标注，不追溯元注解。
 * </p>
 * <p>
 * 本处理器在 Bean 属性填充阶段扫描 {@link RemoteResource} 字段，
 * 按字段声明的接口类型从本地容器获取 Bean 实例并反射写入，
 * 同时按字段名注册为 Spring 单例，使 {@code SpringUtil.getBean(字段名)} 等按名查找可用
 * （如 {@code BaseServiceImpl} 的关联数据解析机制 {@code @QueryMapping(serviceName=...)}）。
 * {@code beanFactory.getBean(Class)} 在仅有一个候选时忽略 {@code autowireCandidate} 标志，
 * 因此 {@link LocalRemoteServiceRegistrar} 注册的 {@code autowireCandidate = false} 的
 * RPC 实现类仍可被正确解析。
 * </p>
 *
 * @author maozi
 */
@Component
public class LocalRemoteResourceBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

	/** Spring Bean 工厂，用于按类型查找本地 Bean */
	private ConfigurableListableBeanFactory beanFactory;

	/** 已注册的单例名称，避免重复注册 */
	private final Set<String> registeredNames = ConcurrentHashMap.newKeySet();

	/**
	 * 注入 Bean 工厂。
	 *
	 * @param beanFactory Spring Bean 工厂
	 */
	@Override
	public void setBeanFactory(@Nonnull BeanFactory beanFactory) {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	/**
	 * 扫描 {@link RemoteResource} 字段并按接口类型从本地容器注入。
	 * <p>
	 * 遍历目标 Bean 的所有字段（含父类继承的字段），找到 {@link RemoteResource} 标注的非静态字段，
	 * 通过 {@code beanFactory.getBean(字段类型)} 从本地容器获取实例，按字段名注册为单例后反射写入。
	 * </p>
	 *
	 * @param pvs      属性值
	 * @param bean     正在创建的 Bean 实例
	 * @param beanName Bean 名称
	 * @return 原始属性值（本处理器不修改属性值，仅通过反射写字段）
	 * @throws BeansException 获取或注入 Bean 时发生异常
	 */
	@Override
	public PropertyValues postProcessProperties(@Nonnull PropertyValues pvs, @Nonnull Object bean, @Nonnull String beanName) throws BeansException {

		ReflectionUtils.doWithFields(bean.getClass(), field -> {
			if (field.isAnnotationPresent(RemoteResource.class) && !Modifier.isStatic(field.getModifiers())) {
				Object dependency = beanFactory.getBean(field.getType());
				registerSingletonIfAbsent(field.getName(), dependency);
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, bean, dependency);
			}
		});

		return pvs;

	}

	/**
	 * 按名称将依赖实例注册为 Spring 单例（若尚未注册）。
	 * <p>
	 * 使 {@code SpringUtil.getBean(字段名)} 等按名查找可用，兼容项目中
	 * {@code BaseServiceImpl} 通过 {@code @QueryMapping(serviceName=...)} 解析关联数据的机制。
	 * </p>
	 *
	 * @param name     单例名称（取自字段名）
	 * @param singleton 依赖实例
	 */
	private void registerSingletonIfAbsent(String name, Object singleton) {
		if (beanFactory != null && registeredNames.add(name)) {
			try {
				beanFactory.registerSingleton(name, singleton);
			} catch (IllegalStateException e) {
				registeredNames.remove(name);
			}
		}
	}

}
