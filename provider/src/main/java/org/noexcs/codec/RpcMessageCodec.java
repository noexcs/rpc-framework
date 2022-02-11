package org.noexcs.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.noexcs.message.RpcMessage;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;

import java.util.Arrays;
import java.util.List;

/**
 * @author com.noexcept
 * @since 1/17/2022 3:56 PM
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) {
        RpcResponseMessage message = (RpcResponseMessage) msg;

        System.out.println(msg);
        log.debug("{}",msg.toString());

        byte[] bytes = new byte[1024];

        try {
            bytes = Serializer.Algorithm.values()[0].serialize(message);
        } catch (Exception e) {
            log.debug("{}",e.getMessage());
            e.printStackTrace();
        }
        int length = bytes.length;
        log.debug("{}",length);
        log.debug("{}",Arrays.toString(bytes));

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
        RpcRequestMessage rpcRequestMessage = Serializer.Algorithm.values()[0].deserialize(RpcRequestMessage.class, bytes);
        out.add(rpcRequestMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
