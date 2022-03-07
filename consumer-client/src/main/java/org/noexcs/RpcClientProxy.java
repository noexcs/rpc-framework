package org.noexcs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class RpcClientProxy extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger ID = new AtomicInteger();

    private static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(32, false), r -> new Thread(r, "rpc-pool-" + ID.getAndIncrement()));

    @SuppressWarnings("unchecked")
    public <T> T getProxy(final Class<T> clazz) {
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(clazz);
//        enhancer.setCallback((MethodInterceptor) (proxy, method, args, methodProxy) -> {
//            int sequenceId = ID.getAndIncrement();
//            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId, clazz.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);
//            Channel channel = RpcClient.getChannel();
//            channel.writeAndFlush(rpcRequestMessage);
//            CompletableFuture<RpcResponseMessage> future = new CompletableFuture<>();
//            ClientHandler.RcpResponses.put(sequenceId, future);
//
//            RpcResponseMessage response = future.get();
//
//            return response.getReturnValue();
//        });
//        return ((T) enhancer.create());
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            int sequenceId = ID.getAndIncrement();
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId, clazz.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);

            Channel channel = RpcClient.getChannel();
            CompletableFuture<RpcResponseMessage> future = new CompletableFuture<>();
            ClientHandler.RcpResponses.put(sequenceId, future);
            channel.writeAndFlush(rpcRequestMessage);

            RpcResponseMessage response = future.get();

            RpcClient.setNextProviderAddress(channel);
            if (response.getReturnValue()==null){
                throw new RuntimeException(response.getExceptionValue().getMessage());
            }
            return response.getReturnValue();
        });
    }
}
