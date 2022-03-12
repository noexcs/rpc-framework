package org.noexcs.spring.boot.autoconfigure;

import org.noexcs.RpcClientProxy;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author noexcs
 * @since 3/12/2022 10:43 PM
 */
public class ClassPathRpcScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annotationClass;

    public ClassPathRpcScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.hasAnnotation("org.noexcs.spring.boot.autoconfigure.RpcService");
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> definitionHolders = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : definitionHolders) {
            ScannedGenericBeanDefinition definition = ((ScannedGenericBeanDefinition) holder.getBeanDefinition());

            String beanClassName = definition.getBeanClassName();
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);

            try {
                definition.getPropertyValues().add("rpcInterface", Class.forName(beanClassName, true, Thread.currentThread().getContextClassLoader()));
            } catch (ClassNotFoundException ignored) {
            }
            definition.setBeanClass(RpcFactoryBean.class);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            definition.setLazyInit(false);
        }
        return definitionHolders;
    }
}
