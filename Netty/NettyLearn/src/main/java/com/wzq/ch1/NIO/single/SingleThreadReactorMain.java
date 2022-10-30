package com.wzq.ch1.NIO.single;

import java.io.IOException;

/**
 * 单线程Reactor服务端启动程序
 *
 * @author wzq
 * @create 2022-10-30 19:33
 */
public class SingleThreadReactorMain {

    // 服务端默认端口
    public final static int DEFAULT_PORT = 7;

    public static void main(String[] args) throws IOException {

        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
        }

        // 启动
        Thread thread = new Thread(new EchoServerReactor(port));
        thread.start();
    }

}
