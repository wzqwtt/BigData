package com.wzq.nio_base.non_blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * 非阻塞模式客户端
 *
 * @author wzq
 * @create 2022-11-15 17:12
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress("localhost", 7));
        System.out.println("wait....");
    }

}
