package org.noexcs.loadBalance.impl;

import org.noexcs.loadBalance.AbstractLoadBalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author com.noexcept
 * @since 1/21/2022 3:42 PM
 */
public class RandomBalance extends AbstractLoadBalance {
    @Override
    public InetSocketAddress select(List<InetSocketAddress> serverList) {
        return serverList.get(new Random().nextInt(serverList.size()));
    }
}
