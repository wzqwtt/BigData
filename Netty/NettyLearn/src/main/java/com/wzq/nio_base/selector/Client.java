package com.wzq.nio_base.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author wzq
 * @create 2022-11-14 21:46
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 7));
//        sc.write(Charset.defaultCharset().encode("0123456789abcdefg\n"));

        // 客户端正常断开
//        sc.close();
        System.out.println("waiting ... ");
    }

}
