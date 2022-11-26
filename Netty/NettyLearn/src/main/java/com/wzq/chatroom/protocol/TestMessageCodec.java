package com.wzq.chatroom.protocol;

import com.wzq.chatroom.message.LoginRequestMessage;
import com.wzq.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-11-25 15:58
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(LogLevel.DEBUG),
                new LengthFieldBasedFrameDecoder(
                        1024, 12, 4, 0, 0),
                new MessageCodecSharable()
        );

        // encoder
        Message msg = new LoginRequestMessage("wzq", "123");
        embeddedChannel.writeOutbound(msg);

        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, msg, buf);

        // 模拟粘包半包
        ByteBuf buf1 = buf.slice(0, 100);
        ByteBuf buf2 = buf.slice(100, buf.readableBytes() - 100);

        embeddedChannel.writeInbound(buf1);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        embeddedChannel.writeInbound(buf2);

    }

}
