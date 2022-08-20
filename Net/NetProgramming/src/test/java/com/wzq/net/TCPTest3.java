package com.wzq.net;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * TCP例题3：从客户端发送文件给服务端，服务端保存到本地之后，并返回”保存成功“给客户端，并关闭相应链接
 *
 * @author wzq
 * @create 2022-08-18 15:42
 */
public class TCPTest3 {

    public final int port = 8899;

    @Test
    public void client() {
        Socket socket = null;
        OutputStream outputStream = null;
        FileInputStream fis = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            socket = new Socket("localhost", port);
            // 发出
            outputStream = socket.getOutputStream();
            fis = new FileInputStream(new File("file/1.jpg"));
            byte[] buffer = new byte[20];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            // 关闭数据的传输
            socket.shutdownOutput();

            // 接收信息
            inputStream = socket.getInputStream();
            baos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            System.out.println(baos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
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
        ServerSocket serverSockert = null;
        Socket socket = null;
        InputStream inputStream = null;
        FileOutputStream fos = null;
        OutputStream outputStream = null;
        try {
            serverSockert = new ServerSocket(port);
            socket = serverSockert.accept();

            // 接受信息
            inputStream = socket.getInputStream();
            fos = new FileOutputStream(new File("file/1Copy2.jpg"));
            byte[] buffer = new byte[20];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            // 发回信息
            outputStream = socket.getOutputStream();
            outputStream.write("保存成功".getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if (serverSockert != null) {
                try {
                    serverSockert.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
