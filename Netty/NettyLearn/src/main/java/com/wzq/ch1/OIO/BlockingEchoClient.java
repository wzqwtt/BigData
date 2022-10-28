package com.wzq.ch1.OIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author wzq
 * @create 2022-10-28 19:12
 */
public class BlockingEchoClient {

    public static int DEFAULT_PORT = 7;
    public static String DEFAULT_HOSTNAME = "localhost";

    public static void main(String[] args) {

        String hostname;
        int port;

        try {
            port = Integer.parseInt(args[0]);
            hostname = args[1];
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
            hostname = DEFAULT_HOSTNAME;
        }

        try (
                Socket echoSocket = new Socket(hostname, port);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo : " + in.readLine());
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
