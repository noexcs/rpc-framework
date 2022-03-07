package org.noexcs;

/**
 * @author noexcs
 * @since 3/7/2022 3:40 PM
 */
public class ServerMain {
    public static void main(String[] args) {
        RpcServer.start(ServerMain.class);
    }
}
