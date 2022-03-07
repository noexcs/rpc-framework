package org.noexcs.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.noexcs.message.RpcMessage;
import org.noexcs.message.RpcRequestMessage;
import org.noexcs.message.RpcResponseMessage;

import java.util.List;

/**
 * @author com.noexcept
 * @since 1/17/2022 3:56 PM
 */
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {
    @Override
    public void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) {
        RpcRequestMessage message = (RpcRequestMessage) msg;

        byte[] bytes = Serializer.Algorithm.values()[0].serialize(message);
        int length = bytes.length;

        ByteBuf buf = ctx.alloc().buffer();
        buf.writeInt(length);
        buf.writeBytes(bytes);
        out.add(buf);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);
        RpcResponseMessage rpcResponseMessage = Serializer.Algorithm.values()[0].deserialize(RpcResponseMessage.class, bytes);
        out.add(rpcResponseMessage);
    }
}
