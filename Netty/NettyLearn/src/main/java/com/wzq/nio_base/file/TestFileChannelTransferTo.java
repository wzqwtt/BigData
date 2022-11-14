package com.wzq.nio_base.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 测试两个channel之间的传输
 *
 * @author wzq
 * @create 2022-11-14 20:41
 */
@Slf4j
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                // 读入数据
                FileChannel from = new FileInputStream("input/words.txt").getChannel();
                // 写出数据
                FileChannel to = new FileOutputStream("input/wordsCopy2.txt").getChannel();
        ) {
            // 因为一次只能传输2G的数据，所以我们循环传输
            long size = from.size();
            // left变量代表还剩余多少个字节
            for (long left = size; left > 0; ) {
                log.debug("position:{}, left:{}", (size - left), left);
                left -= from.transferTo((size - left), left, to);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
