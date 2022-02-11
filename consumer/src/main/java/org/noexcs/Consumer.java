package org.noexcs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.noexcs.codec.RpcMessageCodec;
import org.noexcs.codec.Serializer;
import org.noexcs.handler.ClientHandler;
import org.noexcs.message.RpcResponseMessage;
import org.noexcs.service.HelloService;

/**
 * @author com.noexcept
 * @since 1/10/2022 2:03 AM
 */
public class Consumer {
    public static void main(String[] args) {
        testRpc();

        System.out.println("===============");

        testRpc();
//        try (InputStream inputStream = Consumer.class.getClassLoader().getResourceAsStream("rpc-consumer-config.yml")) {
//            Yaml yaml = new Yaml();
//            Iterable<Object> objects = yaml.loadAll(inputStream);
//            for (Object object : objects) {
//                System.out.println(object.getClass().getName());
//                System.out.println(object);
//            }
//        } catch (IOException e) {
//        }

//        test();
    }

    private static void testRpc() {
        HelloService helloService = new RpcClientProxy().getProxy(HelloService.class);
        String s = helloService.hello("hello");
        System.out.println(s);
    }

    private static void test() {
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0);
        EmbeddedChannel channel
                = new EmbeddedChannel(lengthFieldBasedFrameDecoder, new RpcMessageCodec(), new ClientHandler());


        RpcResponseMessage responseMessage = new RpcResponseMessage(1, "hello", null);

//        byte[] bytes = new Gson().toJson(responseMessage).getBytes(StandardCharsets.UTF_8);

        byte[] bytes = Serializer.Algorithm.values()[0].serialize(responseMessage);
        int length = bytes.length;

        ByteBuf buf = Unpooled.buffer(4 + length);
        buf.writeInt(length);
        buf.writeBytes(bytes);

        channel.writeInbound(buf);
    }
}
