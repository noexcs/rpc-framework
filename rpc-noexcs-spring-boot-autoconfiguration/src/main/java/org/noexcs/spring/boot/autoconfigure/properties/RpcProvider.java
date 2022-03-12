package org.noexcs.spring.boot.autoconfigure.properties;

/**
 * @author noexcs
 * @since 3/12/2022 7:25 PM
 */
public class RpcProvider {

    /**
     * rpc provider host
     */
    String server = "127.0.0.1";

    /**
     * rpc provider port
     */
    Integer port = 8007;

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
}
