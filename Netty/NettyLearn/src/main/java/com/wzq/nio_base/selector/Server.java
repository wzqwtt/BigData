package com.wzq.nio_base.selector;

import com.wzq.nio_base.buffer.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 使用NIO的Selector
 *
 * @author wzq
 * @create 2022-11-15 22:26
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {

        // 创建ServerSocketChannel，配置非阻塞模式，绑定端口
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(7));

        // 创建Selector
        Selector selector = Selector.open();

        // 将ServerSocketChannel注册到Selector中，并关注可连接的事件
        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 也可以使用interestOps方法去关注感兴趣的事件
        // sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register Key: {}", sscKey);

        while (true) {
            // Select
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                //
                SelectionKey key = iterator.next();
                log.debug("key: {}", key);
                // 一定要移除这个key
                iterator.remove();
                if (key.isAcceptable()) {
                    // 如果该key是可连接事件
                    // 获取该key对应的ServerSocketChannel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // ServerSocketChannel获取客户端的SocketChannel
                    SocketChannel sc = channel.accept();
                    // 配置新的SocketChannel位非阻塞模式
                    sc.configureBlocking(false);
                    // 将SocketChannel注册到Selector，关心的事件是可读
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);

                    // 新建一个ByteBuffer做为附件
                    ByteBuffer readBuffer = ByteBuffer.allocate(16);
                    ByteBuffer[] buffers = new ByteBuffer[2];
                    buffers[0] = readBuffer;
                    buffers[1] = null;
                    // 并将buffer作为附件绑定到对应的socketChannel上
                    scKey.attach(buffers);

                    // 成功连接上socketChannel，发送一些数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer writeBuffer = Charset.defaultCharset().encode(sb.toString());

                    // 写入一些数据，返回值是写入的字节数
                    int write = sc.write(writeBuffer);
                    log.debug("写入的字节数：{}", write);

                    // 如果还有字节没有写完，那就让该key关注可写事件
                    if (writeBuffer.hasRemaining()) {
                        // 关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 把未写完的数据绑定到scKey附件
                        buffers[1] = writeBuffer;
                        scKey.attach(buffers);
                    }

                } else if (key.isReadable()) {
                    // 如果是可读事件
                    try {
                        // 获取该key对应的SocketChannel
                        SocketChannel sc = (SocketChannel) key.channel();
                        // 拿到该key绑定的ByteBuffer附件
                        ByteBuffer[] buffers = (ByteBuffer[]) key.attachment();
                        ByteBuffer buffer = buffers[0];
                        // 读取数据
                        int read = sc.read(buffer);
                        log.debug("read={},buffer capacity={}", read, buffer.capacity());

                        if (read == -1) {
                            // 需要拿到read的返回值，如果客户端正常关闭连接，会发送一个 -1
                            // 如果我们什么也不做，程序将陷入死循环，因此判断是否为正常关闭，并且取消该key
                            key.cancel();
                        } else {
                            // buffer拿去切分消息
                            split(buffer);
                            // 如果没有切分出来一条完整消息，就扩容
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                // 将原有的buffer信息填充到新buffer
                                buffer.flip();
                                newBuffer.put(buffer);
                                // 将扩容后的buffer重新加入附件，之前的附件被替换
                                buffers[0] = newBuffer;
                                key.attach(buffers);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        // 如果发生异常（客户端断开），取消该socketChannel
                        key.cancel();
                    }
                } else if (key.isWritable()) {
                    // 如果是可写事件
                    // 取出附件
                    ByteBuffer[] buffers = (ByteBuffer[]) key.attachment();
                    ByteBuffer buffer = buffers[1];
                    // 如果buffer不等于null，那就继续写
                    if (buffer != null) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        int write = sc.write(buffer);
                        log.debug("写入的字节数：{}", write);

                        if (!buffer.hasRemaining()) {
                            // 替换附件
                            buffers[1] = null;
                            key.attach(buffers);
                            // 不再关注可写事件
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        }

    }

    private static void split(ByteBuffer source) {
        // 切换到读模式
        source.flip();
        // 遍历buffer，找`\n`切割点
        for (int i = 0; i < source.limit(); i++) {
            // get(i)方法，position位置不变
            if (source.get(i) == '\n') {
                // 计算这一条信息的长度
                int len = i - source.position() + 1;
                // 开辟一个临时buffer，存储这一条消息
                ByteBuffer target = ByteBuffer.allocate(len);
                // 将这一条消息放到target
                for (int j = 0; j < len; j++) {
                    // get()方法会移动position
                    target.put(source.get());
                }
                // 打印这条信息
                ByteBufferUtil.debugAll(target);
            }
        }
        // buffer可能有没有读取完的数据，所以使用compact压缩
        // 现在是读模式了
        source.compact();
    }

}
