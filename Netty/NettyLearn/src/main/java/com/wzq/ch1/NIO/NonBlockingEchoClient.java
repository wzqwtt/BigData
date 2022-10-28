package com.wzq.ch1.NIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wzq
 * @create 2022-10-28 20:04
 */
public class NonBlockingEchoClient {

    public final static String NAME = "Non Blocking Echo Client";
    public final static int DEFAULT_PORT = 7;
    public final static String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        int port;
        String hostname;

        try {
            port = Integer.parseInt(args[0]);
            hostname = args[1];
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
            hostname = DEFAULT_HOST;
        }

        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            System.err.println(NAME + "异常:" + e.getMessage());
            System.exit(1);
        }

        ByteBuffer writeBuffer = ByteBuffer.allocate(32);
        ByteBuffer readBuffer = ByteBuffer.allocate(32);

        try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                writeBuffer.put(userInput.getBytes());
                writeBuffer.flip();
                writeBuffer.rewind();

                // 写消息到管道
                socketChannel.write(writeBuffer);

                // 管道读消息
                socketChannel.read(readBuffer);

                // 清理缓冲区
                writeBuffer.clear();
                readBuffer.clear();
                System.out.println("echo: " + userInput);
            }

        } catch (UnknownHostException e) {
            System.err.println("不明主机，主机名为：" + hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("不能从主机中获取I/O，主机名为：" + hostname);
            System.exit(1);
        }
    }

}
