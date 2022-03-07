package org.noexcs.service;

import org.noexcs.config.SpringContextConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author com.noexcept
 * @since 1/17/2022 5:58 PM
 */
public class ServiceContainer {

    private static volatile ApplicationContext APPLICATION_CONTEXT = null;

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    public static void initApplicationContext(String basePackage) {
        if (APPLICATION_CONTEXT == null) {
            APPLICATION_CONTEXT = new AnnotationConfigApplicationContext(basePackage, SpringContextConfig.class.getPackage().getName());
        }
    }

    public static <T> T getServiceImpl(Class<T> serviceClass) {
        return APPLICATION_CONTEXT.getBean(serviceClass);
    }
}
