package com.wzq.ch5.transorm;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * KeyBy 按键分区
 *
 * @author wzq
 * @create 2022-09-23 20:53
 */
public class KeyByDemo {


    public static void main(String[] args) throws Exception {
        // 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> source = env.fromElements(
                new Event("wzq", "./home", 1000L),
                new Event("wtt", "./card?id=1", 2000L)
        );

        // 使用匿名内部类
        source.keyBy(new KeySelector<Event, String>() {
            @Override
            public String getKey(Event value) throws Exception {
                return value.user;
            }
        }).print();

        // 使用lambda
        source.keyBy(e -> e.user).print();

        // KeySelector实现类
        source.keyBy(new keySelector()).print();

        // 触发执行
        env.execute();
    }

    public static class keySelector implements KeySelector<Event, String> {

        @Override
        public String getKey(Event value) throws Exception {
            return value.user;
        }
    }


}
