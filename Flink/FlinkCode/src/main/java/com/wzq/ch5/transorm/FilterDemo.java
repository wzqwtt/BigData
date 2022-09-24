package com.wzq.ch5.transorm;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Filter 过滤
 *
 * @author wzq
 * @create 2022-09-18 23:05
 */
public class FilterDemo {

    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // 设置并行度

        // 造数据
        DataStreamSource<Event> stream = env.fromElements(
                new Event("wtt", "./home", 1000L),
                new Event("wzq", "./card", 2000L)
        );

        // 传入匿名内部类
        stream.filter(new FilterFunction<Event>() {
            @Override
            public boolean filter(Event value) throws Exception {
                return value.user.equals("wtt");
            }
        });

        // lambda表达式
        stream.filter((Event e) -> {
            return e.user.equals("wtt");
        });

        // FilterFunction实现类
        stream.filter(new UserFilter());

        // 执行
        env.execute();
    }

    public static class UserFilter implements FilterFunction<Event> {
        @Override
        public boolean filter(Event value) throws Exception {
            return value.user.equals("wtt");
        }
    }
}
