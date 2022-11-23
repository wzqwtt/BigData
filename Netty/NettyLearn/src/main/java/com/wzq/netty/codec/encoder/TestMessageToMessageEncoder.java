package com.wzq.netty.codec.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 测试MessageToMessageEncoder
 *
 * @author wzq
 * @create 2022-11-23 14:20
 */
@Slf4j
public class TestMessageToMessageEncoder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel进行测试
        EmbeddedChannel channel = new EmbeddedChannel(
                // 再来一个Handler查看是否为String类型的数据
                new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.debug(msg.getClass().toString());
                    }
                },
                // 将Int转换为String的编码器
                new MessageToMessageEncoder<Integer>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
                        out.add(String.valueOf(msg));
                    }
                }
        );

        // 往channel写一些int数据
        channel.writeOutbound((Integer) 1, (Integer) 2, (Integer) 3);
    }

}
