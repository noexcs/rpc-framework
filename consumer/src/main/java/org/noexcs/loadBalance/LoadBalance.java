package org.noexcs.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/21/2022 3:39 PM
 */
public interface LoadBalance {

    /**
     * 从所有服务器列表中选取一个
     * @param serverList
     * @return
     */
    InetSocketAddress select(List<InetSocketAddress> serverList);

}
