package com.wzq.nio_base.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-11-14 21:46
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 7));
        System.out.println("waiting...");
    }

}
