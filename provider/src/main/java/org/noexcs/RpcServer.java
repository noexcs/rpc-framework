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
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.codec.RpcMessageCodec;
import org.noexcs.config.SpringContextConfig;
import org.noexcs.registry.nacos.NacosServiceRegistryImpl;
import org.noexcs.service.ServiceContainer;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;

/**
 * Hello world!
 * @author com.noexcept
 */
@Slf4j
public class RpcServer {

    public static void start() {
        log.debug("rpc server starting...");
        ApplicationContext context = ServiceContainer.getApplicationContext();
        SpringContextConfig contextConfig = context.getBean(SpringContextConfig.class);
        new RpcServer().run(contextConfig.getHost(),contextConfig.getPort());
    }

    public void run(String host, int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerChannelHandler serverChannelHandler = new ServerChannelHandler();
        RpcMessageCodec rpcMessageCodec = new RpcMessageCodec();
        LoggingHandler loggingHandler = new LoggingHandler();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,0,4,0,0));
                            pipeline.addLast(loggingHandler);
                            pipeline.addLast(rpcMessageCodec);
                            pipeline.addLast(serverChannelHandler);
                        }
                    });

            ChannelFuture channelFuture = b.bind(host, port).sync();
            log.debug("rpc server started at {}:{}!",host,port);

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
                InetSocketAddress inetSocketAddress = new InetSocketAddress(host,port);
                if (new NacosServiceRegistryImpl(registryServer,registryServerPort).registerService(serviceName, inetSocketAddress)) {
                    log.debug("rpc service has been registered to {} [{}:{}]","nacos", registryServer, registryServerPort);
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
