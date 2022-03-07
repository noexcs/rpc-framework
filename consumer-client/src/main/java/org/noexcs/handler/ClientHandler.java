package org.noexcs.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.noexcs.message.RpcResponseMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author com.noexcept
 * @since 1/10/2022 12:39 AM
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static ConcurrentHashMap<Integer, CompletableFuture<RpcResponseMessage>> RcpResponses = new ConcurrentHashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) {
        CompletableFuture<RpcResponseMessage> completableFuture = RcpResponses.remove(msg.getSequenceId());
        completableFuture.complete(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
