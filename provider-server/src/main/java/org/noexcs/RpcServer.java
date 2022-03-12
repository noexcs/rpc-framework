package org.noexcs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.codec.RpcMessageCodec;
import org.noexcs.config.SpringContextConfig;
import org.noexcs.registry.nacos.NacosServiceRegistryImpl;
import org.noexcs.service.ServiceContainer;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;

/**
 * Hello world!
 * @author noexcs
 */
@Slf4j
public class RpcServer {

    public static void start(Class<?> mainClass) {
        String basePackage = mainClass.getPackage().getName();
        log.debug("rpc server starting...");
        ServiceContainer.initApplicationContext(basePackage);
        ApplicationContext context = ServiceContainer.getApplicationContext();
        SpringContextConfig contextConfig = context.getBean(SpringContextConfig.class);
        new RpcServer().run(contextConfig.getHost(), contextConfig.getPort());
    }

    public static void startBackground(Class<?> mainClass, boolean daemon) {
        String basePackage = mainClass.getPackage().getName();
        ServiceContainer.initApplicationContext(basePackage);
        ApplicationContext context = ServiceContainer.getApplicationContext();
        SpringContextConfig contextConfig = context.getBean(SpringContextConfig.class);
        Thread rpcService = new Thread(() ->
                new RpcServer().run(contextConfig.getHost(), contextConfig.getPort()), "rpc");
        rpcService.setDaemon(daemon);
        rpcService.start();
    }

    private void run(String host, int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerChannelHandler serverChannelHandler = new ServerChannelHandler();
        RpcMessageCodec rpcMessageCodec = new RpcMessageCodec();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(10240, 0, 4, 0, 0));
                            pipeline.addLast(rpcMessageCodec);
                            pipeline.addLast(serverChannelHandler);
                        }
                    });

            ChannelFuture channelFuture = b.bind(host, port).sync();
            log.info("rpc server started at {}:{}!", host, port);

            SpringContextConfig contextConfig = ServiceContainer.getApplicationContext().getBean(SpringContextConfig.class);
            Boolean registryEnabled = contextConfig.getRegistryEnabled();
            if (registryEnabled != null && registryEnabled) {
                String serviceName = contextConfig.getServiceName();
                if (serviceName == null) {
                    serviceName = "rpc-public";
                    log.debug("service name will be rpc-public by default.");
                }
                String registryServer = contextConfig.getRegistryServer();
                Integer registryServerPort = contextConfig.getRegistryServerPort();
                InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
                if (new NacosServiceRegistryImpl(registryServer, registryServerPort).registerService(serviceName, inetSocketAddress)) {
                    log.debug("rpc service has been registered to {} [{}:{}]", "nacos", registryServer, registryServerPort);
                }
            }

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
