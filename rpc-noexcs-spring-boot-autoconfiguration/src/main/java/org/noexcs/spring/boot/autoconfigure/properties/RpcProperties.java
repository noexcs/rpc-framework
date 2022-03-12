package org.noexcs.spring.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author noexcs
 * @since 3/12/2022 7:20 PM
 */
@ConfigurationProperties(prefix = "spring.noexcs.rpc")
public class RpcProperties {
    /**
     * time out that when waiting a rpc response
     */
    Integer timedOut = -1;

    /**
     * how many times to retry when error occurred in invoking a rpc call
     */
    Integer retries = 1;

    /**
     * registry center info
     */
    Registry registry;

    /**
     * rpc provider info which will be used when registry not enabled
     */
    RpcProvider provider;

    public Integer getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Integer timedOut) {
        this.timedOut = timedOut;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public RpcProvider getProvider() {
        return provider;
    }

    public void setProvider(RpcProvider provider) {
        this.provider = provider;
    }
}
