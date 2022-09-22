package com.wzq.ch5.source;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 从文件中读取
 *
 * @author wzq
 * @create 2022-09-18 20:54
 */
public class FromLocalFile {

    public static void main(String[] args) throws Exception {
        // 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // 设置并行度为1
        env.setRuntimeMode(RuntimeExecutionMode.BATCH); //设置Flink运行模式为批处理

        // 从文件中读取数据
        DataStreamSource<String> source = env.readTextFile("input/clicks.csv");
        // 转换为
        SingleOutputStreamOperator<Event> map = source.map((String line) -> {
            String[] split = line.split(",");
            return new Event(split[0], split[1], Long.parseLong(split[2]));
        });
        map.print();

        // 触发执行
        env.execute();
    }

}
