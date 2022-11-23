package com.wzq.netty.codec.encoder;

import com.wzq.netty.bytebuf.MyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 测试MessageToByteEncoder
 *
 * @author wzq
 * @create 2022-11-23 14:10
 */
public class TestMessageToByteEncoder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel测试
        EmbeddedChannel channel = new EmbeddedChannel(
                // 由于是出站，要读取outBound的Handler，因此顺序应该是从tail到head，倒着使用handler
                // 再使用一个Handler接收上游的信息
                new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        MyByteBufUtil.log(buf);
                    }
                },
                // 编码器
                new MessageToByteEncoder<Integer>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
                        out.writeInt(msg);
                    }
                }
        );

        // 写入一些数据到channel
        channel.writeOutbound((Integer) 1, (Integer) 2, (Integer) 3);
    }

}
