package com.wzq.ch5.source;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 从HDFS中读取数据
 *
 * @author wzq
 * @create 2022-09-18 21:11
 */
public class FromHDFSFile {

    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // 设置并行度
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);     // 设置执行模式

        // 读取文件，并转换，最终输出
        env.readTextFile("hdfs://hadoop102:9870/clicks.csv")
                .map((String line) -> {
                    String[] split = line.split(",");
                    return new Event(split[0], split[1], Long.parseLong(split[2]));
                }).print();

        // 触发执行
        env.execute();
    }

}
