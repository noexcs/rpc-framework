package org.noexcs;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.concurrent.CountDownLatch;

/**
 * @author com.noexcept
 * @since 1/21/2022 12:26 AM
 */
public class Main {
    public static void main(String[] args) throws Exception {
        NamingService namingService = NamingFactory.createNamingService("192.168.11.1:8848");



        namingService.registerInstance("nacos.test.3", "11.11.11.11", 8888, "TEST1");
        namingService.registerInstance("nacos.test.3", "11.11.11.11", 8888, "TEST2");

        System.in.read();

        namingService.deregisterInstance("nacos.test.3", "11.11.11.11", 8888, "TEST1");

        new CountDownLatch(1).await();

    }
}
