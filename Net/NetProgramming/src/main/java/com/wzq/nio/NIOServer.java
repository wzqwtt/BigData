package com.wzq.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO快速入门，要求：
 * <p>
 * 1、编写一个NIO入门案例，实现服务器端和客户端之间的数据简单通讯（非阻塞）
 * <p>
 * 2、目的：理解NIO非阻塞网络编程机制
 *
 * @author wzq
 * @create 2022-08-29 15:47
 */
public class NIOServer {

    private static int port = 6666;

    public static void main(String[] args) throws Exception {

        // 创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一个Selector对象
        Selector selector = Selector.open();

        // 绑定一个端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 把ServerSocketChannel注册到Selector，关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循环等待客户端连接
        while (true) {
            // 等待1秒钟，如果没有事件发生，返回
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }

            // 如果返回的>0，就获取到相关的集合
            // 1、如果返回的>0，表示已经获取到关注的事件
            // 2、selector.selectedKeys(); 返回关注事件的集合
            // 通过selectionKeys可以反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                // 获取到selectionKey
                SelectionKey key = keyIterator.next();
                // 根据这个key对应的通道发生的事件做相应的处理

                // 如果是 OP_ACCEPT，有新的客户端连接
                if (key.isAcceptable()) {
                    // 给该客户端生成一个 socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    // 将socketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    // 将socketChannel，注册到Selector，关注事件 OP_READ，同时给socketChannel关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                // 如果是 OP_READ，读取
                if (key.isReadable()) {
                    // 通过key 反向获取到对应的Channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 获取到该Channel关联的Buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    // 读取
                    channel.read(buffer);
                    System.out.println("from 客户端 " + new String(buffer.array()));
                }

                // 手动从集合中移除当前的SelectionKey，防止进行重复操作
                keyIterator.remove();
            }
        }
    }

}
