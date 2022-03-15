package org.noexcs.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.codec.Impl.HessianSerializer;
import org.noexcs.config.SpringContextConfig;
import org.noexcs.message.RpcMessage;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;
import org.noexcs.service.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/17/2022 3:56 PM
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {

    static Serializer serializer;

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcMessageCodec.class);

    static {
        SpringContextConfig contextConfig = ServiceContainer.getApplicationContext().getBean(SpringContextConfig.class);
        String serializerClass = contextConfig.getSerializerClass();
        Class<?> clazz;
        try {
            clazz = Class.forName(serializerClass);
            serializer = ((Serializer) clazz.getConstructor().newInstance());
            LOGGER.info("Use {} as serializer.", serializerClass);
        } catch (ClassNotFoundException e) {
            LOGGER.error("specified serializer class not found, use builtin Hessian serializer!");
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("specified serializer class initiation error, use builtin Hessian serializer!");
        }
        if (serializer == null) {
            serializer = new HessianSerializer();
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) {
        RpcResponseMessage message = (RpcResponseMessage) msg;

        System.out.println(msg);
        log.debug("{}", msg.toString());

        byte[] bytes = new byte[1024];

        try {
            bytes = serializer.serialize(message);
        } catch (Exception e) {
            log.debug("{}", e.getMessage());
            e.printStackTrace();
        }
        int length = bytes.length;
        log.debug("{}", length);
        log.debug("{}", Arrays.toString(bytes));

        ByteBuf buf = ctx.alloc().buffer(4 + length);
        buf.writeInt(length);
        buf.writeBytes(bytes);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes);
        RpcRequestMessage rpcRequestMessage = serializer.deserialize(RpcRequestMessage.class, bytes);
        out.add(rpcRequestMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
