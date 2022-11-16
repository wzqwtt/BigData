package com.wzq.nio_base.non_blocking;

import com.wzq.nio_base.buffer.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * 非阻塞NIO示例代码
 *
 * @author wzq
 * @create 2022-11-15 17:06
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 0、创建一个ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 1、创建一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2、绑定端口
        ssc.bind(new InetSocketAddress(7));
        // 3、配置非阻塞
        ssc.configureBlocking(false);

        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
//            log.debug("connecting...");
            // 连接
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                log.debug("connected... {}", sc);
                // SocketChannel也要配置非阻塞
                sc.configureBlocking(false);
                channels.add(sc);
            }

            for (SocketChannel channel : channels) {
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("read: {}", channel);
                }
            }
        }
    }

}
