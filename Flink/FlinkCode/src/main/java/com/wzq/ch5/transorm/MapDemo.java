package com.wzq.ch5.transorm;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * map映射
 *
 * @author wzq
 * @create 2022-09-18 22:57
 */
public class MapDemo {

    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // 设置并行度

        // 造数据
        DataStreamSource<Event> stream = env.fromElements(
                new Event("wtt", "./home", 1000L),
                new Event("wzq", "./card", 2000L)
        );

        // 传入匿名内部类，实现MapFunction
        stream.map(new MapFunction<Event, String>() {
            @Override
            public String map(Event value) throws Exception {
                return value.user;
            }
        });

        // lambda表达式
        stream.map((Event e) -> {
            return e.user;
        });

        // 传入MapFunction的实现类
        stream.map(new UserExtractor());

        // 执行
        env.execute();
    }

    public static class UserExtractor implements MapFunction<Event, String> {

        @Override
        public String map(Event value) throws Exception {
            return value.user;
        }
    }

}
