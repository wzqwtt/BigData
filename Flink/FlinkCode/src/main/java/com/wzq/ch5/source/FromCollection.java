package com.wzq.ch5.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

/**
 * 从集合中读取
 *
 * @author wzq
 * @create 2022-09-18 20:31
 */
public class FromCollection {

    public static void main(String[] args) throws Exception {

        // 1、创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(1);

        // 2、从集合中读取
        ArrayList<Event> list = new ArrayList<>();
        list.add(new Event("wzq", "./home", 1000L));
        list.add(new Event("wtt", "./cart", 2000L));

        DataStream<Event> stream = env.fromCollection(list);

        // 也可以不构建集合，从元素读取fromElements
//        DataStreamSource<Event> source = env.fromElements(
//                new Event("wzq", "./home", 1000L),
//                new Event("wtt", "./cart", 2000L)
//        );

        stream.print();

        // 3、触发执行
        env.execute();

    }

}
