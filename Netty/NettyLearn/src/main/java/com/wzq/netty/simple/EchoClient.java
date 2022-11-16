package com.wzq.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * @author wzq
 * @create 2022-11-02 21:46
 */
public class EchoClient {

    static final String DEFAULT_HOSTNAME = "127.0.0.1";
    static final int DEFAULT_PORT = 7;

    public static void main(String[] args) throws InterruptedException {
        int port;
        String host;

        try {
            port = Integer.parseInt(args[0]);
            host = args[1];
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
            host = DEFAULT_HOSTNAME;
        }

        // 配置客户端
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new EchoClientHandler());

        // 连接到服务器
        ChannelFuture f = b.connect(host, port).sync();

        Channel channel = f.channel();
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                writeBuffer.put(userInput.getBytes());
                writeBuffer.flip();
                writeBuffer.rewind();

                // 转为ByteBuf
                ByteBuf buf = Unpooled.copiedBuffer(writeBuffer);

                // 写消息到管道
                channel.writeAndFlush(buf);

                // 清理缓冲区
                writeBuffer.clear();
            }
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } finally {
            group.shutdownGracefully();
        }
    }

}
