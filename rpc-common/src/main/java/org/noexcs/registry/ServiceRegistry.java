package org.noexcs.registry;

import java.net.InetSocketAddress;

/**
 * @author com.noexcept
 * @since 1/21/2022 1:21 AM
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName    服务名称
     * @param inetSocketAddress 服务地址
     * @return 是否注册成功
     */
    boolean registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
