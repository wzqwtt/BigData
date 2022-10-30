package com.wzq.ch1.NIO.single;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 单线程Reactor模式
 *
 * @author wzq
 * @create 2022-10-30 19:32
 */
public class EchoServerReactor implements Runnable {

    Selector selector;
    ServerSocketChannel serverSocket;

    // 构造器
    public EchoServerReactor(int port) throws IOException {
        serverSocket = ServerSocketChannel.open();
        // 绑定端口号
        serverSocket.bind(new InetSocketAddress(port));
        // 设置为非阻塞
        serverSocket.configureBlocking(false);

        // 打开Selector
        selector = Selector.open();
        // 注册selector到serverSocket
        SelectionKey key = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        // 绑定AcceptorHandler新连接处理器到selectKey
        key.attach(new AcceptorHandler());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    dispatch(key);
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 反应器的事件分发
    void dispatch(SelectionKey key) {
        Runnable handler = (Runnable) key.attachment();
        if (handler != null) {
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocket.accept();
                if (channel != null) {
                    // 调用EchoHandler的构造方法建立新连接
                    new EchoHandler(selector,channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
