package org.noexcs.spring.boot.autoconfigure.properties;

/**
 * @author noexcs
 * @since 3/12/2022 7:25 PM
 */
public class Registry {
    /**
     * whether to enable registry
     */
    boolean enabled = false;

    /**
     * registry type
     */
    String type = "nacos";

    /**
     * registry server
     */
    String server = "127.0.0.1";

    /**
     * registry port
     */
    Integer port = 8848;

    /**
     * load balancer class full qualified name
     */
    String loadBalancer = "org.noexcs.loadBalance.impl.RandomBalance";

    /**
     * service name that register to registry
     */
    String serviceName = "rpc-public";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
