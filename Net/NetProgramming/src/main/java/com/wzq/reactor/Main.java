package com.wzq.reactor;

import java.io.IOException;

/**
 * @author wzq
 * @create 2022-09-02 21:25
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(5566);
        reactor.run();
    }
}
