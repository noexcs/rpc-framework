package org.noexcs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.handler.ClientHandler;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author com.noexcept
 * @since 1/10/2022 11:06 AM
 */
@Slf4j
public class RpcClientProxy extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger ID = new AtomicInteger();

    private static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(32, false), r -> new Thread(r, "rpc-pool-" + ID.getAndIncrement()));

    @SuppressWarnings("unchecked")
    public <T> T getProxy(final Class<T> clazz) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            int sequenceId = ID.getAndIncrement();
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId, clazz.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);

            Channel channel = RpcClient.getChannel();
            CompletableFuture<RpcResponseMessage> future = new CompletableFuture<>();
            ClientHandler.RcpResponses.put(sequenceId, future);
            channel.writeAndFlush(rpcRequestMessage);

            RpcResponseMessage response;
            try {
                response = future.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.info("rpc request timed out: {}.{}", clazz.getName(), method.getName());
                return null;
            }

            RpcClient.setNextProviderAddress(channel);
            if (response.getReturnValue() == null) {
                log.error(response.getExceptionValue().getMessage());
                return null;
            }
            return response.getReturnValue();
        });
    }
}
