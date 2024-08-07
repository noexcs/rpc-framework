package org.noexcs;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.message.RpcMessage;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;
import org.noexcs.service.ServiceContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

/**
 * @author noexcs
 * @since 1/10/2022 12:12 AM
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        RpcRequestMessage requestMessage = (RpcRequestMessage) msg;
        SocketAddress remoteAddress = ctx.channel().remoteAddress();

        String className = requestMessage.getInterfaceName();
        Class<?> clazz = Class.forName(className);

        Object result;
        RpcResponseMessage responseMessage;
        responseMessage = new RpcResponseMessage(requestMessage.getSequenceId(), null, null);
        try {
            Object serviceImpl = ServiceContainer.getServiceImpl(clazz);
            Method method = clazz.getDeclaredMethod(requestMessage.getMethodName(), requestMessage.getParameterTypes());

            method.setAccessible(true);
            result = method.invoke(serviceImpl, requestMessage.getParameterValue());
            responseMessage.setReturnValue(result);
            log.info("Received a rpc call: {}#{} from {}", className, requestMessage.getMethodName(), remoteAddress);
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            responseMessage.setExceptionValue(new Exception(e.getMessage()));
        }
        ctx.channel().writeAndFlush(responseMessage);
        log.info("Returned {}", responseMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info(cause.getMessage());
    }
}
