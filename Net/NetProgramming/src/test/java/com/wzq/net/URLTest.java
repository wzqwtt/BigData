package com.wzq.net;

import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * URL：对应互联网某一资源地址
 *
 * @author wzq
 * @create 2022-08-18 16:43
 */
public class URLTest {

    @Test
    public void urlTest() {
        try {
            URL url = new URL("https://github.com/wzqwtt");
            // 获取URL的协议名
            System.out.println(url.getProtocol());
            // 获取URL的主机名
            System.out.println(url.getHost());
            // 获取URL的端口号
            System.out.println(url.getPort());
            // 获取URL的文件路径
            System.out.println(url.getFile());
            // 获取URL的文件名
            System.out.println(url.getFile());
            // 获取URL的查询名
            System.out.println(url.getQuery());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
