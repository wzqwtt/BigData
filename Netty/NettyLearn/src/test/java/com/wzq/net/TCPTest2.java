package com.wzq.net;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP例题2：客户端发送文件给服务端，服务端将文件保存在本地
 *
 * @author wzq
 * @create 2022-08-18 15:18
 */
public class TCPTest2 {

    public final int port = 8888;

    @Test
    public void client() {
        Socket socket = null;
        OutputStream outputStream = null;
        FileInputStream fis = null;
        try {
            socket = new Socket("localhost", port);
            outputStream = socket.getOutputStream();
            fis = new FileInputStream(new File("file/1.jpg"));

            byte[] buffer = new byte[10];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void server() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();

            System.out.println("收到来自" + socket.getInetAddress().getHostAddress() + "的文件，文件正在保存！");
            inputStream = socket.getInputStream();
            // 文件使用流，最后保存到本地
            fos = new FileOutputStream(new File("file/1Server.jpg"));

            byte[] buffer = new byte[10];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件保存完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
