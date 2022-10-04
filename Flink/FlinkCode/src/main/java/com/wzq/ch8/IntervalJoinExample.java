package com.wzq.ch8;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * 间隔查询IntervalJoin
 * 使用一个用户的下订单的事件和这个用户最近十分钟的浏览数据做一个联结查询
 *
 * @author wzq
 * @create 2022-10-04 21:32
 */
public class IntervalJoinExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Tuple3<String, String, Long>> orderStream = createDataStream(env,
                Tuple3.of("Marry", "order-1", 5000L),
                Tuple3.of("Alice", "order-2", 5000L),
                Tuple3.of("Bob", "order-3", 20000L),
                Tuple3.of("Alice", "order-4", 20000L),
                Tuple3.of("Cary", "order-5", 51000L)
        );

        DataStream<Event> clickStream = createDataStream(env,
                new Event("Bob", "./cart", 2000L),
                new Event("Alice", "./prod?id=100", 3000L),
                new Event("Alice", "./prod?id=200", 3500L),
                new Event("Bob", "./prod?id=2", 2500L),
                new Event("Alice", "./prod?id=300", 36000L),
                new Event("Bob", "./home", 30000L),
                new Event("Bob", "./prod?id=1", 23000L),
                new Event("Bob", "./prod?id=3", 33000L)
        );

        orderStream.keyBy(data -> data.f0)
                .intervalJoin(clickStream.keyBy(data -> data.user))
                .between(Time.seconds(-5), Time.seconds(10))
                .process(new ProcessJoinFunction<Tuple3<String, String, Long>, Event, String>() {
                    @Override
                    public void processElement(Tuple3<String, String, Long> left, Event right, ProcessJoinFunction<Tuple3<String, String, Long>, Event, String>.Context ctx, Collector<String> out) throws Exception {
                        out.collect(left + " => " + right);
                    }
                })
                .print();

        env.execute();
    }

    public static <IN> DataStream<IN> createDataStream(StreamExecutionEnvironment env,
                                                       IN... data) {
        return env.fromCollection(Arrays.asList(data))
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<IN>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<IN>() {
                                    @Override
                                    public long extractTimestamp(IN element, long recordTimestamp) {
                                        if (element.getClass() == Tuple3.class) {
                                            return ((Tuple3<String, String, Long>) element).f2;
                                        } else if (element.getClass() == Event.class) {
                                            return ((Event) element).timestamp;
                                        } else {
                                            // 报错
                                            throw new IllegalArgumentException("类型错误！");
                                        }
                                    }
                                })
                );
    }

}
