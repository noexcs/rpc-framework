package org.noexcs.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/21/2022 1:24 AM
 */
public interface ServiceDiscover {

    /**
     * 根据集群和服务名称发现服务
     * @return
     * @param serviceName 服务名称
     */
    List<InetSocketAddress> discoverService(String serviceName);

}
