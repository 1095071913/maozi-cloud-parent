package com.mountain.applicationcontext;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 功能说明:	上下文
 * 功能作者:	彭晋龙
 * 创建日期:	2019.8.14
 * 版权归属:	忧伤-蓝河
 */

@Component
public class ApplicationContextHolder implements ApplicationContextAware, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextHolder.class);

    private static ApplicationContext applicationContext;

    /**
     * 获取存储在静态变量中的 ApplicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 从静态变量 applicationContext 中获取 Bean，自动转型成所赋值对象的类型
     *
     * @param name
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量 applicationContext 中获取 Bean，自动转型成所赋值对象的类型
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(clazz);
    }

    /**
     * 实现 DisposableBean 接口，在 Context 关闭时清理静态变量
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        logger.debug("清除 SpringContext 中的 ApplicationContext: {}", applicationContext);
        applicationContext = null;
    }

    /**
     * 实现 ApplicationContextAware 接口，注入 Context 到静态变量中
     *
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    /**
     * 断言 Context 已经注入
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null, "applicationContext 属性未注入，请在 spring-context.xml 配置中定义 ApplicationContextHolder");
    }
}