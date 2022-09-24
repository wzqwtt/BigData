package com.wzq.ch5.transorm;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 简单聚合
 *
 * @author wzq
 * @create 2022-09-23 21:06
 */
public class SimpleReduceDemo {

    public static void main(String[] args) throws Exception {
        // 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//        env.setRuntimeMode(RuntimeExecutionMode.BATCH);     // 设置为批处理模式

        DataStreamSource<Tuple2<String, Integer>> source = env.fromElements(
                Tuple2.of("a", 1),
                Tuple2.of("a", 3),
                Tuple2.of("b", 3),
                Tuple2.of("b", 4)
        );

        source.keyBy(e -> e.f0).sum(1).print("指定位置");
        source.keyBy(e -> e.f0).sum("f1").print("指定字段名称");

        source.keyBy(e -> e.f0).max(1).print("指定位置");
        source.keyBy(e -> e.f0).max("f1").print("指定字段名称");

        source.keyBy(e -> e.f0).min(1).print("指定位置");
        source.keyBy(e -> e.f0).min("f1").print("指定字段名称");

        source.keyBy(e -> e.f0).maxBy(1).print("指定位置");
        source.keyBy(e -> e.f0).maxBy("f1").print("指定字段名称");

        source.keyBy(e -> e.f0).minBy(1).print("指定位置");
        source.keyBy(e -> e.f0).minBy("f1").print("指定字段名称");

        // 如果是POJO类，则只能指定字段名称
        DataStreamSource<Event> eventDS = env.fromElements(
                new Event("wzq", "./card", 1000L),
                new Event("wzq", "./index", 2000L),
                new Event("wtt", "./home", 3000L),
                new Event("wtt", "./buy", 4000L)
        );

        eventDS.keyBy(e -> e.user).max("timestamp").print();


        // 触发执行
        env.execute();
    }

}
