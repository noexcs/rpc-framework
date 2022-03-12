package org.noexcs.spring.boot.autoconfigure;

import org.noexcs.RpcClientProxy;
import org.noexcs.spring.boot.autoconfigure.properties.RpcProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * @author noexcs
 * @since 3/12/2022 6:14 PM
 */
@Configuration
@AutoConfigureAfter()
@Import(AutoConfiguredRpcServiceRegistrar.class)
@EnableConfigurationProperties(RpcProperties.class)
public class AutoConfigRpcService {

    @Bean
    @ConditionalOnMissingBean
    RpcClientProxy rpcClientProxy() {
        return new RpcClientProxy();
    }

}
