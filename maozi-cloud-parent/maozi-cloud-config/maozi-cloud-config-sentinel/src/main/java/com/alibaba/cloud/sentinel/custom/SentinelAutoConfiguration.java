/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.sentinel.custom;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.datasource.converter.AuthorityRuleJsonConverter;
import com.alibaba.cloud.sentinel.datasource.converter.JsonConverter;
import com.alibaba.cloud.sentinel.datasource.converter.XmlConverter;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import static com.alibaba.cloud.sentinel.SentinelConstants.BLOCK_PAGE_URL_CONF_KEY;
import static com.alibaba.csp.sentinel.config.SentinelConfig.setConfig;

/**
 * Sentinel 自动配置类
 * <p>
 * 该类是 Spring Cloud Sentinel 的核心自动配置入口，负责在 Spring Boot 应用启动时
 * 自动初始化 Sentinel 的各项配置，包括：
 * <ul>
 *   <li>将 Spring 配置文件中的 Sentinel 属性映射为系统属性</li>
 *   <li>注册 Sentinel 资源切面（{@link SentinelResourceAspect}），使 @SentinelResource 注解生效</li>
 *   <li>注册 RestTemplate 的 Sentinel 后置处理器</li>
 *   <li>注册数据源处理器（{@link SentinelDataSourceHandler}）</li>
 *   <li>注册各种规则转换器（JSON 和 XML 格式）</li>
 * </ul>
 * 当配置项 spring.cloud.sentinel.enabled 为 true（默认）时，此配置类才会生效。
 * </p>
 * <p>
 * <b>注：</b>本类为覆盖 Spring Cloud Alibaba 依赖 jar 中同名原生类的本地副本
 * （同包同名类在 classpath 上优先于 jar 内类加载），用于按项目需要定制原生自动配置行为，
 * 主要定制点：授权规则的 JSON 转换器由原生 {@code JsonConverter} 替换为
 * {@link AuthorityRuleJsonConverter}（兼容 Sentinel 控制台下发的实体包装格式）；
 * 后续升级依赖版本时需同步比对原生类变更。
 * </p>
 *
 * @author xiaojing
 * @author jiashuai.xie
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelAutoConfiguration {

	/** 项目名称，优先使用 project.name，其次使用 spring.application.name */
	@Value("${project.name:${spring.application.name:}}")
	private String projectName;

	/** Sentinel 配置属性 */
	@Autowired
	private SentinelProperties properties;

	/**
	 * 初始化方法，在 Bean 构造完成后执行
	 * <p>
	 * 将 Spring Boot 配置文件中的 Sentinel 配置属性设置到系统属性中，
	 * 供 Sentinel 内部模块使用。除阻塞页面外，其余配置项仅当对应的系统属性尚未被设置时才会写入；
	 * 阻塞页面（block-page）只要配置了值就会无条件写入。
	 * 配置项包括：日志目录、日志 PID 开关、应用名称、传输端口、控制台地址、
	 * 心跳间隔、客户端 IP、字符集、指标文件大小、指标文件数量、冷启动因子、阻塞页面等。
	 * </p>
	 */
	@PostConstruct
	private void init() {
		// 设置日志目录
		if (StringUtils.isEmpty(System.getProperty(LogBase.LOG_DIR))
				&& StringUtils.hasText(properties.getLog().getDir())) {
			System.setProperty(LogBase.LOG_DIR, properties.getLog().getDir());
		}
		// 设置日志文件名是否包含进程 ID
		if (StringUtils.isEmpty(System.getProperty(LogBase.LOG_NAME_USE_PID))
				&& properties.getLog().isSwitchPid()) {
			System.setProperty(LogBase.LOG_NAME_USE_PID,
					String.valueOf(properties.getLog().isSwitchPid()));
		}
		// 设置应用名称
		if (StringUtils.isEmpty(System.getProperty(SentinelConfig.APP_NAME_PROP_KEY))
				&& StringUtils.hasText(projectName)) {
			System.setProperty(SentinelConfig.APP_NAME_PROP_KEY, projectName);
		}
		// 设置 Sentinel 客户端与控制台通信的端口
		if (StringUtils.isEmpty(System.getProperty(TransportConfig.SERVER_PORT))
				&& StringUtils.hasText(properties.getTransport().getPort())) {
			System.setProperty(TransportConfig.SERVER_PORT,
					properties.getTransport().getPort());
		}
		// 设置 Sentinel 控制台地址
		if (StringUtils.isEmpty(System.getProperty(TransportConfig.CONSOLE_SERVER))
				&& StringUtils.hasText(properties.getTransport().getDashboard())) {
			System.setProperty(TransportConfig.CONSOLE_SERVER,
					properties.getTransport().getDashboard());
		}
		// 设置客户端与控制台的心跳发送间隔（毫秒）
		if (StringUtils.isEmpty(System.getProperty(TransportConfig.HEARTBEAT_INTERVAL_MS))
				&& StringUtils
						.hasText(properties.getTransport().getHeartbeatIntervalMs())) {
			System.setProperty(TransportConfig.HEARTBEAT_INTERVAL_MS,
					properties.getTransport().getHeartbeatIntervalMs());
		}
		// 设置客户端 IP 地址（用于控制台识别客户端）
		if (StringUtils.isEmpty(System.getProperty(TransportConfig.HEARTBEAT_CLIENT_IP))
				&& StringUtils.hasText(properties.getTransport().getClientIp())) {
			System.setProperty(TransportConfig.HEARTBEAT_CLIENT_IP,
					properties.getTransport().getClientIp());
		}
		// 设置指标文件的字符集编码
		if (StringUtils.isEmpty(System.getProperty(SentinelConfig.CHARSET))
				&& StringUtils.hasText(properties.getMetric().getCharset())) {
			System.setProperty(SentinelConfig.CHARSET,
					properties.getMetric().getCharset());
		}
		// 设置单个指标文件的大小
		if (StringUtils
				.isEmpty(System.getProperty(SentinelConfig.SINGLE_METRIC_FILE_SIZE))
				&& StringUtils.hasText(properties.getMetric().getFileSingleSize())) {
			System.setProperty(SentinelConfig.SINGLE_METRIC_FILE_SIZE,
					properties.getMetric().getFileSingleSize());
		}
		// 设置指标文件的总数量
		if (StringUtils
				.isEmpty(System.getProperty(SentinelConfig.TOTAL_METRIC_FILE_COUNT))
				&& StringUtils.hasText(properties.getMetric().getFileTotalCount())) {
			System.setProperty(SentinelConfig.TOTAL_METRIC_FILE_COUNT,
					properties.getMetric().getFileTotalCount());
		}
		// 设置流控冷启动因子
		if (StringUtils.isEmpty(System.getProperty(SentinelConfig.COLD_FACTOR))
				&& StringUtils.hasText(properties.getFlow().getColdFactor())) {
			System.setProperty(SentinelConfig.COLD_FACTOR,
					properties.getFlow().getColdFactor());
		}
		// 设置自定义的流控阻塞页面 URL
		if (StringUtils.hasText(properties.getBlockPage())) {
			setConfig(BLOCK_PAGE_URL_CONF_KEY, properties.getBlockPage());
		}

		// 如果配置了 eager（饥饿初始化），则提前初始化 Sentinel
		if (properties.isEager()) {
			InitExecutor.doInit();
		}

	}

	/**
	 * 创建 Sentinel 资源切面 Bean
	 * <p>
	 * 该切面用于处理 {@code @SentinelResource} 注解标注的方法，
	 * 实现方法级别的流量控制和熔断降级
	 * </p>
	 *
	 * @return SentinelResourceAspect 实例
	 */
	@Bean
	@ConditionalOnMissingBean
	public SentinelResourceAspect sentinelResourceAspect() {
		return new SentinelResourceAspect();
	}

	/**
	 * 创建 Sentinel Bean 后置处理器
	 * <p>
	 * 该处理器用于对 RestTemplate 进行增强，使其支持 Sentinel 的流控和熔断功能
	 * </p>
	 *
	 * @param applicationContext Spring 应用上下文
	 * @return SentinelBeanPostProcessor 实例
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
	@ConditionalOnProperty(name = "resttemplate.sentinel.enabled", havingValue = "true",
			matchIfMissing = true)
	public SentinelBeanPostProcessor sentinelBeanPostProcessor(
			ApplicationContext applicationContext) {
		return new SentinelBeanPostProcessor(applicationContext);
	}

	/**
	 * 创建 Sentinel 数据源处理器
	 * <p>
	 * 用于动态加载和管理 Sentinel 规则数据源（如 Nacos、ZooKeeper、Apollo 等）
	 * </p>
	 *
	 * @param beanFactory          Spring Bean 工厂
	 * @param sentinelProperties   Sentinel 配置属性
	 * @param env                  Spring 环境变量
	 * @return SentinelDataSourceHandler 实例
	 */
	@Bean
	@ConditionalOnMissingBean
	public SentinelDataSourceHandler sentinelDataSourceHandler(
			DefaultListableBeanFactory beanFactory, SentinelProperties sentinelProperties,
			Environment env) {
		return new SentinelDataSourceHandler(beanFactory, sentinelProperties, env);
	}

	/**
	 * Sentinel 规则转换器配置类
	 * <p>
	 * 当 classpath 中存在 Jackson ObjectMapper 时生效，
	 * 提供各种 Sentinel 规则的 JSON 和 XML 格式转换器
	 * </p>
	 */
	@ConditionalOnClass(ObjectMapper.class)
	@Configuration(proxyBeanMethods = false)
	protected static class SentinelConverterConfiguration {

		/**
		 * JSON 格式的 Sentinel 规则转换器配置
		 * <p>
		 * 配置 ObjectMapper 忽略未知属性，并注册各类规则的 JSON 转换器 Bean
		 * </p>
		 */
		@Configuration(proxyBeanMethods = false)
		protected static class SentinelJsonConfiguration {

			/** Jackson JSON 序列化/反序列化工具 */
			private ObjectMapper objectMapper = new ObjectMapper();

			/**
			 * 构造函数，配置 ObjectMapper 忽略 JSON 中的未知属性，避免反序列化失败
			 */
			public SentinelJsonConfiguration() {
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
			}

			/**
			 * 创建流控规则的 JSON 转换器
			 *
			 * @return JSON 流控规则转换器
			 */
			@Bean("sentinel-json-flow-converter")
			public JsonConverter jsonFlowConverter() {
				return new JsonConverter(objectMapper, FlowRule.class);
			}

			/**
			 * 创建降级规则的 JSON 转换器
			 *
			 * @return JSON 降级规则转换器
			 */
			@Bean("sentinel-json-degrade-converter")
			public JsonConverter jsonDegradeConverter() {
				return new JsonConverter(objectMapper, DegradeRule.class);
			}

			/**
			 * 创建系统规则的 JSON 转换器
			 *
			 * @return JSON 系统规则转换器
			 */
			@Bean("sentinel-json-system-converter")
			public JsonConverter jsonSystemConverter() {
				return new JsonConverter(objectMapper, SystemRule.class);
			}

			/**
			 * 创建授权规则的 JSON 转换器
			 * <p>使用自定义的 AuthorityRuleJsonConverter 进行转换</p>
			 *
			 * @return JSON 授权规则转换器
			 */
			@Bean("sentinel-json-authority-converter")
			public JsonConverter jsonAuthorityConverter() {
				return new AuthorityRuleJsonConverter(objectMapper);
			}

			/**
			 * 创建热点参数规则的 JSON 转换器
			 *
			 * @return JSON 热点参数规则转换器
			 */
			@Bean("sentinel-json-param-flow-converter")
			public JsonConverter jsonParamFlowConverter() {
				return new JsonConverter(objectMapper, ParamFlowRule.class);
			}

		}

		/**
		 * XML 格式的 Sentinel 规则转换器配置
		 * <p>
		 * 当 classpath 中存在 Jackson XmlMapper 时生效，
		 * 配置 XmlMapper 忽略未知属性，并注册各类规则的 XML 转换器 Bean
		 * </p>
		 */
		@ConditionalOnClass(XmlMapper.class)
		@Configuration(proxyBeanMethods = false)
		protected static class SentinelXmlConfiguration {

			/** Jackson XML 序列化/反序列化工具 */
			private XmlMapper xmlMapper = new XmlMapper();

			/**
			 * 构造函数，配置 XmlMapper 忽略 XML 中的未知属性，避免反序列化失败
			 */
			public SentinelXmlConfiguration() {
				xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
			}

			/**
			 * 创建流控规则的 XML 转换器
			 *
			 * @return XML 流控规则转换器
			 */
			@Bean("sentinel-xml-flow-converter")
			public XmlConverter xmlFlowConverter() {
				return new XmlConverter(xmlMapper, FlowRule.class);
			}

			/**
			 * 创建降级规则的 XML 转换器
			 *
			 * @return XML 降级规则转换器
			 */
			@Bean("sentinel-xml-degrade-converter")
			public XmlConverter xmlDegradeConverter() {
				return new XmlConverter(xmlMapper, DegradeRule.class);
			}

			/**
			 * 创建系统规则的 XML 转换器
			 *
			 * @return XML 系统规则转换器
			 */
			@Bean("sentinel-xml-system-converter")
			public XmlConverter xmlSystemConverter() {
				return new XmlConverter(xmlMapper, SystemRule.class);
			}

			/**
			 * 创建授权规则的 XML 转换器
			 *
			 * @return XML 授权规则转换器
			 */
			@Bean("sentinel-xml-authority-converter")
			public XmlConverter xmlAuthorityConverter() {
				return new XmlConverter(xmlMapper, AuthorityRule.class);
			}

			/**
			 * 创建热点参数规则的 XML 转换器
			 *
			 * @return XML 热点参数规则转换器
			 */
			@Bean("sentinel-xml-param-flow-converter")
			public XmlConverter xmlParamFlowConverter() {
				return new XmlConverter(xmlMapper, ParamFlowRule.class);
			}

		}

	}

}
