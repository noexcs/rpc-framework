package org.noexcs.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.noexcs.registry.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @author com.noexcept
 * @since 1/21/2022 1:36 AM
 */
public class NacosServiceRegistryImpl implements ServiceRegistry {

    private final String registryServer;

    private final Integer registryServerPort;

    public NacosServiceRegistryImpl(String registryServer, Integer registryServerPort) {
        this.registryServer = registryServer;
        this.registryServerPort = registryServerPort;
    }

    @Override
    public  boolean registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        NamingService service;
        try {
            service = NamingFactory.createNamingService(registryServer+":"+registryServerPort);
            String hostAddress = inetSocketAddress.getAddress().getHostAddress();
            service.registerInstance(rpcServiceName, hostAddress, inetSocketAddress.getPort());
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }
}
