package com.wzq.net;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 一、网络编程中有两个主要问题：
 * 1、如何准确的定位网络上的一个或多台主机；定位主机上的特定的应用
 * 2、找到主机后如何可靠高效的进行数据传输
 * <p>
 * 二、网络编程中的两个要素
 * 1、IP和端口号
 * 2、提供网络通信协议：TCP/IP(应用层、传输层、网络层、物理+数据链路层)
 * <p>
 * 三、通信要素一：IP和端口号
 * 1、IP：唯一的标识Internet上的计算机（通信实体）
 * 2、在Java中使用InetAddress类代表IP
 * 3、IP分类：IPv4和IPv6；万维网和局域网的区别
 * 4、域名：www.baidu.com
 * 5、本地回路地址：127.0.0.1 对应 localhost
 * 6、如何实例化InetAddress: 两个方法：getByName(String host)、getLocalHost()
 * 该对象常用的两个方法：getHostName()获取域名、getHostAddress() IP地址
 * 7、端口号：正在计算机上运行的进程
 * 要求：不同的进程要有不同的端口号
 * 范围：一个16位的整数，范围：0~65535
 * 8、端口号和IP地址组合在一起，得出一个网络套接字：Socket
 *
 * @author wzq
 * @create 2022-08-18 13:57
 */
public class InetAddressTest {

    /**
     * InetAddress测试
     */
    @Test
    public void test1() {
        try {
            // 使用IP地址
            InetAddress inet1 = InetAddress.getByName("192.168.10.1");
            System.out.println(inet1);

            // 使用域名
            InetAddress inet2 = InetAddress.getByName("www.baidu.com");
            System.out.println(inet2);

            // 获取本机
            InetAddress inet3 = InetAddress.getLocalHost();
            System.out.println(inet3);

            // 获取域名
            String hostName = inet2.getHostName();
            System.out.println(hostName);
            // 获取IP
            String hostAddress = inet2.getHostAddress();
            System.out.println(hostAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
        }
    }

}
