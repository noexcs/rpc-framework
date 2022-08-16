package org.noexcs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.config.Config;
import org.noexcs.handler.ClientHandler;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;

import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author com.noexcept
 * @since 1/10/2022 11:06 AM
 */
@Slf4j
public class RpcClientProxy extends ChannelInboundHandlerAdapter {

    private final AtomicInteger ID = new AtomicInteger();

    private RpcClient rpcClient;

    private ConcurrentHashMap<Class<?>, Object> map = new ConcurrentHashMap<>();

    public RpcClientProxy(String host, int port) {
        rpcClient = new RpcClient(host, port);
    }

    public RpcClientProxy() {
        rpcClient = new RpcClient();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(final Class<T> clazz) {
        T proxyInstance = (T) map.get(clazz);
        if (proxyInstance == null) {
            synchronized (this) {
                if (proxyInstance == null) {
                    proxyInstance = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
                        int sequenceId = ID.getAndIncrement();
                        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId, clazz.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);

                        Channel channel = rpcClient.getChannel();
                        CompletableFuture<RpcResponseMessage> future = new CompletableFuture<>();
                        ClientHandler.RcpResponses.put(sequenceId, future);
                        channel.writeAndFlush(rpcRequestMessage);

                        RpcResponseMessage response = null;
                        int retries = Config.getRetries();
                        int round = 0;
                        do {
                            try {
                                round++;
                                response = future.get(Config.getTimedOut(), TimeUnit.SECONDS);
                                break;
                            } catch (TimeoutException e) {
                                log.info("rpc request timed out: {}.{}", clazz.getName(), method.getName());
                                if (round < retries) {
                                    log.info("Attempt No.{} request", round + 1);
                                }
                            }
                        } while (round < retries);

                        rpcClient.setNextProviderAddress(channel);

                        if (response == null) {
                            log.info("Retries exhausted！");
                            ClientHandler.RcpResponses.remove(sequenceId);
                            return null;
                        }

                        if (response.getReturnValue() == null) {
                            log.error(response.getExceptionValue().getMessage());
                            return null;
                        }
                        return response.getReturnValue();
                    });
                }
            }
            map.put(clazz, proxyInstance);
        }
        return proxyInstance;
    }
}
