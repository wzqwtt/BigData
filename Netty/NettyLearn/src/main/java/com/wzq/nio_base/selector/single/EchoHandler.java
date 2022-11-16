package com.wzq.nio_base.selector.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-10-30 20:08
 */
public class EchoHandler implements Runnable {

    final SocketChannel channel;
    final SelectionKey key;
    final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    // 处理器实例的状态：发送和接收，一个连接对应一个处理器实例
    static final int RECIEVING = 0, SENDING = 1;

    int state = RECIEVING;

    public EchoHandler(Selector selector, SocketChannel c) throws IOException {
        channel = c;
        // 设置非阻塞
        c.configureBlocking(false);
        // 取得选择键，再设置感兴趣的IO事件
        key = channel.register(selector, 0);

        // 将Handler自身作为选择器的附件，一个连接对应一个处理器实例
        key.attach(this);

        // 注册Read就绪事件
        key.interestOps(SelectionKey.OP_READ);
        // ??
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            if (state == SENDING) {
                // 发送状态，把数据写入连接通道
                channel.write(byteBuffer);
                // byteBuffer切换成写模式，写完后，就准备开始从通道读
                byteBuffer.clear();
                // 注册read就绪事件，开始接收客户端数据
                key.interestOps(SelectionKey.OP_READ);
                // 修改状态，进入接收状态
                state = RECIEVING;
            } else if (state == RECIEVING) {
                // 接收状态，从通道读取数据
                int length = 0;
                while ((length = channel.read(byteBuffer)) > 0) {
                    System.out.println(new String(byteBuffer.array(), 0, length));
                }
                // 读完后，反转byteBuffer的读写模式
                byteBuffer.flip();
                // 准备写数据到通道，注册write就绪事件
                key.interestOps(SelectionKey.OP_WRITE);
                // 注册完成后，进入发送状态
                state = SENDING;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
