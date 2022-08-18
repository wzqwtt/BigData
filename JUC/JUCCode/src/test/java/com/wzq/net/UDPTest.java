package com.wzq.net;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * UDP例子
 *
 * @author wzq
 * @create 2022-08-18 16:34
 */
public class UDPTest {

    public final int port = 9090;

    /**
     * 发送端
     */
    @Test
    public void sender() {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();

            String str = "我是UDP sender";
            byte[] data = str.getBytes(StandardCharsets.UTF_8);

            DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length, InetAddress.getLocalHost(), port);

            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }

    }

    /**
     * 接收端
     */
    @Test
    public void receiver() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[100];
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
            socket.receive(packet);
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

}
