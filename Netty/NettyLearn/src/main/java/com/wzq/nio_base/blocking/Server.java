package com.wzq.nio_base.blocking;

import com.wzq.nio_base.buffer.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * 使用NIO来理解线程的阻塞模式
 *
 * @author wzq
 * @create 2022-11-14 21:39
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 使用NIO来理解线程的阻塞模式
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1、创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 2、绑定监听端口
        ssc.bind(new InetSocketAddress(7));

        // 3、连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4、accept，建立与客户端的连接， SocketChannel用于客户端和服务端的通信
            log.debug("connecting...");
            SocketChannel sc = ssc.accept();
            log.debug("connected... {}", sc);
            // 将新建立的连接加入到集合中
            channels.add(sc);
            // 遍历已建立连接的集合，看是否有需要读取的数据
            for (SocketChannel channel : channels) {
                log.debug("before read... {}", channel);
                channel.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugRead(buffer);
                buffer.clear();
                log.debug("after read... {}", channel);
            }
        }
    }

}
