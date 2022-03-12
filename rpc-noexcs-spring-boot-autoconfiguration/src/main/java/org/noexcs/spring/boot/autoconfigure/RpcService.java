package org.noexcs.spring.boot.autoconfigure;

import java.lang.annotation.*;

/**
 * @author noexcs
 * @since 3/12/2022 6:50 PM
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    /**
     * @since 1.0
     */
    int timedOut() default -1;

    /**
     * @since 1.0
     */
    int retries() default 1;
}
