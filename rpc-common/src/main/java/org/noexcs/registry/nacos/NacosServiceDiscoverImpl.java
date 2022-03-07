package org.noexcs.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.registry.ServiceDiscover;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/21/2022 1:51 AM
 */
@Slf4j
public class NacosServiceDiscoverImpl implements ServiceDiscover {

    static String serverIP = "127.0.0.1";

    static String port = "8848";

    @Override
    public List<InetSocketAddress> discoverService(String serviceName) {

        ArrayList<InetSocketAddress> addresses = null;
        try {
            NamingService service = NamingFactory.createNamingService(serverIP + ":" + port);
            List<Instance> allInstances = service.getAllInstances(serviceName);
            addresses = new ArrayList<>(allInstances.size());
            log.info("Discovered {} {} service!", allInstances.size(), serviceName);
            for (Instance instance : allInstances) {
                addresses.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
                log.info("{}", instance);
            }
        } catch (NacosException e) {
            log.info("No {} service discovered!", serviceName);
            e.printStackTrace();
        }
        return addresses;
    }
}
