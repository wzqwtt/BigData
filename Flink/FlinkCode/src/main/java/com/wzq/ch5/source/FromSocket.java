package com.wzq.ch5.source;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 从socket读取数据
 *
 * @author wzq
 * @create 2022-09-18 21:31
 */
public class FromSocket {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从socket中读取数据
        env.socketTextStream("hadoop102", 7777)
                .map((String line) -> {
                    String[] split = line.split(",");
                    return new Event(split[0], split[1], Long.parseLong(split[2]));
                }).print();

        env.execute();
    }

}
