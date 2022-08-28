package com.wzq.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO案例：
 * <p>
 * 1、使用BIO模型编写一个服务器端，监听6666端口，当有客户端连接时，就启动一个线程与之通信
 * <p>
 * 2、要求使用线程池机制改善，可以连接多个客户端
 * <p>
 * 3、服务器端可以接受客户端发送的数据（telnet方式即可）
 *
 * @author wzq
 * @create 2022-08-21 15:44
 */
public class BIOServer {

    static final int port = 6666;

    public static void main(String[] args) throws Exception {
        // 思路
        // 1、创建一个线程池
        // 2、如果有客户端连接，就创建一个线程，与之通讯（单独写一个方法）
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // 创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器启动了...");

        while (true) {
            // 监听，等待客户端连接
            // TODO accept main线程阻塞
            Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端 ：" + socket.getInetAddress().getHostName());
            // 创建一个线程，与之通信（单独写一个方法）
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handle(socket); // 和客户端通信
                }
            });
        }

    }

    // 编写一个handler方法，和客户端通信
    public static void handle(Socket socket) {
        InputStream inputStream = null;
        try {
            System.out.println("线程信息：id = " + Thread.currentThread().getId() + " 名字：" + Thread.currentThread().getName());

            inputStream = socket.getInputStream();
            int len;
            byte[] buffer = new byte[1024];
            // TODO read 本线程阻塞
            while ((len = inputStream.read(buffer)) != -1) {
                System.out.println("线程信息：id = " + Thread.currentThread().getId() + " 名字：" + Thread.currentThread().getName());
                System.out.println(new String(buffer, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
