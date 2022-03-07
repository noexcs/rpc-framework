package org.noexcs.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/19/2022 11:59 AM
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    public InetSocketAddress doSelect(List<InetSocketAddress> serverList) {
        if (serverList == null || serverList.size() == 0) {
            return null;
        }
        if (serverList.size() == 1) {
            return serverList.get(0);
        }
        return select(serverList);
    }
}
