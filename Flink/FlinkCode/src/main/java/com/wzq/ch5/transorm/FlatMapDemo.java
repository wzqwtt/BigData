package com.wzq.ch5.transorm;

import akka.stream.impl.fusing.Collect;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * FlatMap扁平映射
 *
 * @author wzq
 * @create 2022-09-22 21:14
 */
public class FlatMapDemo {

    public static void main(String[] args) throws Exception {

        // 建立链接
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> dataStreamSource = env.fromElements(
                new Event("wzq", "./home", 1000L),
                new Event("wtt", "./card", 2000L)
        );

        // 匿名内部类
        dataStreamSource.flatMap(new FlatMapFunction<Event, String>() {
            @Override
            public void flatMap(Event value, Collector<String> out) throws Exception {
                if (value.user.equals("wtt")) {
                    out.collect(value.user);
                } else {
                    out.collect(value.user);
                    out.collect(value.url);
                }
            }
        }).print();

        // lambda表达式
        // 因为存在泛型擦除，所以需要加上一个returns指定泛型类型
        dataStreamSource.flatMap((Event e, Collector<String> out) -> {
            if (e.user.equals("wtt")) {
                out.collect(e.user);
            } else {
                out.collect(e.user);
                out.collect(e.url);
            }
        }).returns(Types.STRING).print();

        // FlatMapFunction实现类
        dataStreamSource.flatMap(new UserFlatMap()).print();

        // 触发执行
        env.execute();
    }

    public static class UserFlatMap implements FlatMapFunction<Event, String> {

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            if (value.user.equals("wtt")) {
                out.collect(value.user);
            } else {
                out.collect(value.user);
                out.collect(value.url);
            }
        }
    }

}
