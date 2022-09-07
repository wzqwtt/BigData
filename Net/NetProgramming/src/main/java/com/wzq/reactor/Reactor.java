package com.wzq.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * Recator负责监听 OP_ACCEPT事件
 *
 * @author wzq
 * @create 2022-09-02 20:53
 */
public class Reactor {

    ServerSocketChannel serverSocketChannel;
    Selector selector;

    public Reactor(int port) throws IOException {
        // 创建channel和selector
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();

        // 设置非阻塞和绑定端口号
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));

        // 把Channel注册到Selector进行监听，监听连接的Accept事件
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道
        selectionKey.attach(new Acceptor(selector, serverSocketChannel));
    }

    public void run() {
        // 当线程没有被通知中断
        while (!Thread.interrupted()) {
            try {
                // 一直阻塞到某个注册的通道有事件就绪
                selector.select();

                // 每一个注册的通道都对应一个SelectionKey
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey selectionKey : selected) {
                    // 分发处理
                    dispatch(selectionKey);
                }
                // 移除避免重复处理
                selected.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        // 虽然这里用Runnable作为处理类的抽象类，但并没有当成线程来是呀，而是当成普通的抽象和类，直接调用run方法执行
        Runnable r = (Runnable) selectionKey.attachment();
        if (r != null) {
            r.run();
        }
    }

}
