package com.wzq.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO相关工具类
 *
 * @author wzq
 * @create 2022-08-31 21:14
 */
public class IOUtil {

    /**
     * 关闭流对象
     *
     * @param obj 传递一个需要关闭的流对象
     */
    public static void closeQuitely(Closeable obj) {
        if (obj == null) return;
        try {
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
