package org.noexcs.spring.boot.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author noexcs
 * @since 3/13/2022 9:01 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AutoConfiguredRpcServiceRegistrar.class)
public @interface RpcScan {
    String basePackage() default "";
}
