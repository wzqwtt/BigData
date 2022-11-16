package com.wzq.nio_base.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wzq
 * @create 2022-10-28 19:37
 */
public class NonBlockingEchoServer {

    public final static String NAME = "Non Blocking Echo Server";
    public final static int DEFAULT_PORT = 7;

    public static ServerSocketChannel serverChannel;
    public static Selector selector;

    public static void main(String[] args) {

        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
            System.out.println(NAME + "采用默认端口: " + DEFAULT_PORT);
        }

        try {
            serverChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(port);

            serverChannel.bind(address);
            // 设置非阻塞
            serverChannel.configureBlocking(false);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println(NAME + "已启动，端口: " + port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {

            try {
                selector.select();
            } catch (IOException e) {
                System.out.println(NAME + "异常！" + e.getMessage());
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    // 可连接
                    if (key.isAcceptable()) {
                        acceptable(key);
                    }

                    // 可读
                    if (key.isReadable()) {
                        readable(key);
                    }

                    // 可写
                    if (key.isWritable()) {
                        writeable(key);
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                    }
                }
            }

        }

    }

    // 可连接
    public static void acceptable(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();

        System.out.println(NAME + "接受客户端的链接: " + client);

        // 设置为非阻塞
        client.configureBlocking(false);

        // 客户端注册到Selector
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

        // 分配缓存区
        ByteBuffer buffer = ByteBuffer.allocate(100);
        // 将缓存区附加到clientKey
        clientKey.attach(buffer);
    }

    // 可读
    public static void readable(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer output = (ByteBuffer) key.attachment();

        client.read(output);

        System.out.println(client.getRemoteAddress() + " -> " + NAME + ":" + output.toString());
        key.interestOps(SelectionKey.OP_WRITE);
    }

    // 可写
    public static void writeable(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer output = (ByteBuffer) key.attachment();

        output.flip();

        client.write(output);

        System.out.println(NAME + " -> " + client.getRemoteAddress() + ":" + output.toString());

        output.compact();
        key.interestOps(SelectionKey.OP_READ);
    }

}
