package org.noexcs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.registry.ServiceDiscover;
import org.noexcs.registry.nacos.NacosServiceDiscoverImpl;
import org.noexcs.codec.RpcMessageCodec;
import org.noexcs.config.Config;
import org.noexcs.handler.ClientHandler;
import org.noexcs.loadBalance.AbstractLoadBalance;
import org.noexcs.loadBalance.impl.RandomBalance;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author com.noexcept
 */
@Slf4j
public class RpcClient {

    private static volatile Channel channel;

    private static String HOST;

    private static int PORT;

    private static AbstractLoadBalance LOAD_BALANCE = null;

    private static ServiceDiscover SERVICE_DISCOVER = null;

    private static String SERVICE_NAME;

    private static final ThreadLocal<Channel> CHANNEL_THREAD_LOCAL = new ThreadLocal<>();

    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = (ReentrantReadWriteLock.ReadLock) READ_WRITE_LOCK.readLock();

    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = (ReentrantReadWriteLock.WriteLock) READ_WRITE_LOCK.writeLock();

    private static List<InetSocketAddress> cachedServerList;

    static {
        if (Config.getHasRegistry()) {
            try {
                Class<?> loadBalanceClazz = Class.forName(Config.getLoadBalanceType());
                LOAD_BALANCE = (AbstractLoadBalance) loadBalanceClazz.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                if (LOAD_BALANCE == null) {
                    LOAD_BALANCE = new RandomBalance();
                }
                e.printStackTrace();
            }
            if ("nacos".equals(Config.getRegistryType())) {
                SERVICE_DISCOVER = new NacosServiceDiscoverImpl();
            }
            SERVICE_NAME = Config.getServiceName();

            cachedServerList = SERVICE_DISCOVER.discoverService(SERVICE_NAME);
            // every three seconds update the providers' addresses
            new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "scheduled-service-discover")).schedule(new Runnable() {
                @Override
                public void run() {
                    cachedServerList = SERVICE_DISCOVER.discoverService(SERVICE_NAME);
                }
            }, 3, TimeUnit.SECONDS);
            setNextProviderAddress(null);
        } else {
            HOST = Config.getProviderServer();
            PORT = Config.getProviderPort();
        }
    }

    public static Channel getChannel() {
        if (Config.getHasRegistry()) {
            return initChannel();
        } else {
            if (RpcClient.channel == null) {
                synchronized (RpcClient.class) {
                    if (RpcClient.channel == null) {
                        channel = initChannel();
                    }
                }
            }
            return RpcClient.channel;
        }
    }

    private static Channel initChannel() {
        READ_LOCK.lock();
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            ClientHandler messageHandler = new ClientHandler();
            RpcMessageCodec rpcMessageCodec = new RpcMessageCodec();
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) {
                                ChannelPipeline pipeline = channel.pipeline();
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0));
                                pipeline.addLast(loggingHandler);
                                pipeline.addLast(rpcMessageCodec);
                                pipeline.addLast(messageHandler);
                            }
                        });
                Channel channel = b.connect(HOST, PORT).sync().channel();
                channel.closeFuture().addListener((ChannelFutureListener) future -> group.shutdownGracefully());
                return channel;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            READ_LOCK.unlock();
        }
        return null;
    }

    /**
     * Reset or init the connection that had completed a rpc call just now when registry center exists and select next rpc provider address for next rpc call.
     */
    public static void setNextProviderAddress(Channel channel) {
        if (Config.getHasRegistry()) {
            WRITE_LOCK.lock();
            try {
                if (channel != null) {
                    channel.close();
                    channel = null;
                }
                InetSocketAddress address = LOAD_BALANCE.doSelect(cachedServerList);
                HOST = address.getHostString();
                PORT = address.getPort();
            } finally {
                WRITE_LOCK.unlock();
            }
        }
    }
}
