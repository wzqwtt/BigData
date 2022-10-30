package com.wzq.ch1.OIO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wzq
 * @create 2022-10-28 21:22
 */
public class ReactorServer {

    public final static int DEFAULT_PORT = 7;

    public static void main(String[] args) {

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
        }

        Thread thread = new Thread(new ConnectionPerThread(port));
        thread.setName("OIO-Server");
        thread.start();
    }
}

class ConnectionPerThread implements Runnable {

    private int port;

    public ConnectionPerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                // 接收到一个链接之后，为socket创建连接，新建一个专属处理器对象
                Handler handler = new Handler(socket);
                // 创建一个新线程，专门负责一个连接的处理
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 处理器，这里将内容回显到客户端
class Handler implements Runnable {

    final Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try (
                    // 服务端接收客户端输入
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 服务端往客户端输出
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // 读取数据
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    out.println(inputLine);
                    System.out.println(Thread.currentThread().getName() + " -> " + socket.getRemoteSocketAddress() + ":" + inputLine);
                }

            } catch (IOException e) {
                System.err.println(Thread.currentThread().getName() + "异常！");
                break;
            }
        }
    }
}