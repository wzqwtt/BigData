package com.wzq.ch1.OIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Blocking Echo Server
 *
 * @author wzq
 * @create 2022-10-28 17:01
 */
public class BlockingEchoServer {

    public static int DEFAULT_PORT = 7;

    public static void main(String[] args) {
        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
        }

        ServerSocket serverSocket = null;

        try {

            serverSocket = new ServerSocket(port);
            System.out.println("Blocking Echo Server 已启动，端口：" + port);
        } catch (IOException e) {
            System.out.println("Blocking Echo Server 启动异常，端口：" + port);
            System.out.println(e.getMessage());
        }

        try (
                // 接受客户端建立连接诶，生成socket实例
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 接收客户端的信息
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
                System.out.println("Blocking Echo Server -> " + clientSocket.getRemoteSocketAddress() + ":" + inputLine);
            }
        } catch (IOException e) {
            System.out.println("Blocking Echo Server异常！" + e.getMessage());
        }
    }

}
