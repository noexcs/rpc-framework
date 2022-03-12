package org.noexcs;

import java.util.concurrent.CountDownLatch;

/**
 * @author noexcs
 * @since 3/7/2022 3:40 PM
 */
public class ServerMain {
    public static void main(String[] args) throws InterruptedException {
        RpcServer.startBackground(ServerMain.class, true);
        System.out.println("服务器已启动");
        new CountDownLatch(1).await();
    }
}
