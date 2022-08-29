package com.wzq.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-08-29 20:16
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {
        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞模式
        socketChannel.configureBlocking(false);

        // 提供服务器端的IP和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);

        // 连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            // 当没有完成
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作...");
            }
        }

        // ...如果连接成功，就发送数据
        String str = "hello,wzq,王~";
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        // 发送数据 -> 将Buffer写入到socketChannel
        socketChannel.write(buffer);
        System.out.println("数据发送成功");
//        System.in.read();   // 代码停在这里
    }

}
