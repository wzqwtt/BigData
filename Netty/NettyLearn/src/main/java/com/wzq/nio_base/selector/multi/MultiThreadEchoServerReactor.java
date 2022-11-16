package com.wzq.nio_base.selector.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程版Reactor
 *
 * @author wzq
 * @create 2022-11-02 20:11
 */
public class MultiThreadEchoServerReactor {

    ServerSocketChannel serverSocket;
    AtomicInteger next = new AtomicInteger(0);
    // 选择器集合，引入多个选择器
    Selector[] selectors = new Selector[2];
    // 引入多个子反应器
    SubReactor[] subReactors = null;

    MultiThreadEchoServerReactor(int port) throws IOException {
        // 初始化多个选择器
        selectors[0] = Selector.open();     // 用于监听新连接事件
        selectors[1] = Selector.open();     // 用于监听传输事件

        serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);

        // 第一个选择器，负责监控新连接事件
        SelectionKey sk = serverSocket.register(selectors[0], SelectionKey.OP_ACCEPT);
        // 绑定Handler：新连接监控Handler绑定到SelectionKey（选择键）
        sk.attach(new AcceptorHandler());

        // 第一个子反应器，负责第一个选择器的新连接事件分发
        SubReactor subReactor1 = new SubReactor(selectors[0]);

        // 第二个子反应器，负责第二个选择器的传输事件的分发
        SubReactor subReactor2 = new SubReactor(selectors[1]);

        subReactors = new SubReactor[]{subReactor1, subReactor2};
    }

    private void startService() {
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }


    // 子反应器，负责事件分发，但是不负责事件处理
    class SubReactor implements Runnable {

        final Selector selector;

        SubReactor(Selector selector) {
            this.selector = selector;
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

        void dispatch(SelectionKey key) {
            Runnable handler = (Runnable) key.attachment();
            if (handler != null) {
                handler.run();
            }
        }

    }

    // Handler：新连接处理器
    class AcceptorHandler implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocket.accept();

                if (channel != null) {
                    new MultiThreadEchoHandler(selectors[1], channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        MultiThreadEchoServerReactor multiThreadEchoServerReactor = new MultiThreadEchoServerReactor(7);
        multiThreadEchoServerReactor.startService();
    }

}
