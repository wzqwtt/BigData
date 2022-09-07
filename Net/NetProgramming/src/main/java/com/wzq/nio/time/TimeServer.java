package com.wzq.nio.time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * NIO TimeServer，可以启动多个服务端
 *
 * @author wzq
 * @create 2022-08-30 23:06
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 什么也不做
            }
        }

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        // 启动一个线程
        new Thread(timeServer, "NIO-MultiplexerTimeServer001").start();
    }

}

class MultiplexerTimeServer implements Runnable {

    private Selector selector;  // 多路服用选择器

    private ServerSocketChannel serverSocketChannel;    // 服务端Channel

    /**
     * 初始化多路复用器、绑定监听端口
     *
     * @param port 端口
     */
    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            // 绑定端口，设置最大连接数是1024个
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 注册channel到selector，负责监听的事件是ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start the time server...");
            System.exit(1);
        }
    }

    @Override
    public void run() {

    }
}