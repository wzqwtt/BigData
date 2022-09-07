package com.wzq.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-09-02 21:17
 */
public class Handler implements Runnable {

    Selector selector;
    SocketChannel socket;
    SelectionKey sk;

    ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    ByteBuffer outputBuffer = ByteBuffer.allocate(1024);

    public Handler(Selector selector, SocketChannel socket) throws IOException {
        this.selector = selector;
        this.socket = socket;
        // 设置非阻塞
        this.socket.configureBlocking(false);

        // 把Channel注册到Selector进行监听，监听读缓冲区数据就绪事件
        sk = socket.register(selector, SelectionKey.OP_READ);

        // 把事件处理类本身附着到SelectionKey，方便识别通道和后续处理
        sk.attach(this);
    }

    @Override
    public void run() {
        if (sk.isReadable()) {
            read();
        } else if (sk.isWritable()) {
            write();
        }
    }

    public void read() {
        try {
            while (socket.read(inputBuffer) > 0) {
                inputBuffer.flip(); // 切换到读模式
                while (inputBuffer.hasRemaining()) {
                    System.out.println((char) inputBuffer.get());
                }
                inputBuffer.clear(); // 切换到写模式

                // 注册监听写缓冲区空闲事件，与前面通过channel注册到selector的写法等价
                sk.interestOps(sk.interestOps() | SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() {
        try {
            outputBuffer.put("YYDS-ABCD-EFG\n".getBytes());
            outputBuffer.flip();
            while (outputBuffer.hasRemaining()) {
                socket.write(outputBuffer);
            }
            outputBuffer.clear();
            sk.interestOps(sk.interestOps() & ~SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
