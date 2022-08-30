package com.wzq.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 群聊系统服务器端
 *
 * @author wzq
 * @create 2022-08-30 18:54
 */
public class GroupChatServer {

    // 定义相关的属性
    private Selector selector;
    private ServerSocketChannel listenChannel;  // 负责监听
    private static final int PORT = 6667;

    // 构造器
    public GroupChatServer() {
        try {
            // 得到选择器
            selector = Selector.open();
            // 初始化ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listenChannel.configureBlocking(false);
            // 将listener注册到Selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 监听
    public void listen() {
        try {
            // 循环监听
            while (true) {
                int count = selector.select(2000);

                // count > 0 有事件要处理
                if (count > 0) {
                    // 遍历得到的SelectionKey集合
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        // 取出这个SelectionKey
                        SelectionKey key = keyIterator.next();

                        // 监听到ACC连接事件
                        if (key.isAcceptable()) {
                            // 得到socketChannel
                            SocketChannel socketChannel = listenChannel.accept();
                            // 设置非阻塞
                            socketChannel.configureBlocking(false);
                            // 将该socketChannel注册到Selector，关注读事件
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + "上线");
                        }

                        // 监听到读事件
                        if (key.isReadable()) {
                            // 处理读（专门写一个方法）
                            readData(key);
                        }

                        // 把当前的key删除，防止重复处理
                        keyIterator.remove();
                    }
                } else {
                    System.out.println("wait...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取客户端消息
     *
     * @param key 服务端通过key反向拿到与该客户端的通道
     */
    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            // 取到关联的channel
            channel = (SocketChannel) key.channel();
            // 创建一个Buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 从Channel中读取到数据
            int count = channel.read(buffer);

            // 根据count的值做处理
            if (count > 0) {
                // 把缓冲区的数据转换成字符串
                String msg = new String(buffer.array());
                System.out.println("from client: " + msg);

                // 向其他客户端转发消息(去掉自己)，写一个方法
                sendInfoToOtherClients(msg, channel);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了...");
                // 取消注册，
                key.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 转发消息给其他客户(通道)
    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        // 遍历所有注册到Selector上的SocketChannel并排除自己
        for (SelectionKey key : selector.keys()) {
            // 通过key取出对应的socketChannel
            Channel targetChannel = key.channel();
            // 排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                SocketChannel dest = (SocketChannel) targetChannel;
                // 将msg存储到Buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将Buffer的数据写入到Channel
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        // 创建一个服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

}
