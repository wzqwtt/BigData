package com.wzq.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-09-02 20:59
 */
public class Acceptor implements Runnable {

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            // 调用accept()方法从已完成链接的队列中拿到连接进行处理
            SocketChannel clientSocket = serverSocketChannel.accept();

            if (clientSocket != null) {
                // 绑定连接对应的事件处理器实例
                new Handler(selector, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
