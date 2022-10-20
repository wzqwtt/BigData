package com.wzq.ch9;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * 在 Flink SQL 中，支持两条流的全量 Join，语法如下：
 * SELECT * FROM A INNER JOIN B WHERE A.id = B.id；
 * 这样一条 SQL 语句要慎用，因为 Flink 会将 A 流和 B 流的所有数据都保存下来，然后进
 * 行 Join。不过在这里我们可以用列表状态变量来实现一下这个 SQL 语句的功能。
 *
 * @author wzq
 * @create 2022-10-07 22:15
 */
public class ListStateExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Tuple3<String, String, Long>> stream1 = createDataStream(env,
                Tuple3.of("a", "stream-1", 1000L),
                Tuple3.of("b", "stream-1", 2000L)
        );

        DataStream<Tuple3<String, String, Long>> stream2 = createDataStream(env,
                Tuple3.of("a", "stream-2", 3000L),
                Tuple3.of("b", "stream-2", 4000L)
        );

        stream1.keyBy(r -> r.f0)
                .connect(stream2.keyBy(r -> r.f0))
                .process(new CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>() {
                    // 声明状态
                    private ListState<Tuple3<String, String, Long>> stream1ListState;
                    private ListState<Tuple3<String, String, Long>> stream2ListState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        // 注册状态
                        stream1ListState = getRuntimeContext().getListState(
                                new ListStateDescriptor<Tuple3<String, String, Long>>(
                                        "stream1-list",
                                        Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)
                                )
                        );
                        stream2ListState = getRuntimeContext().getListState(
                                new ListStateDescriptor<Tuple3<String, String, Long>>(
                                        "stream2-list",
                                        Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)
                                )
                        );
                    }

                    // 处理第一条流
                    @Override
                    public void processElement1(Tuple3<String, String, Long> left,
                                                CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx,
                                                Collector<String> out) throws Exception {
                        stream1ListState.add(left);
                        for (Tuple3<String, String, Long> right : stream2ListState.get()) {
                            out.collect(left + " => " + right);
                        }
                    }

                    // 处理第二条流
                    @Override
                    public void processElement2(Tuple3<String, String, Long> right,
                                                CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx,
                                                Collector<String> out) throws Exception {
                        stream2ListState.add(right);
                        for (Tuple3<String, String, Long> left : stream1ListState.get()) {
                            out.collect(left + " => " + right);
                        }
                    }
                }).print();

        env.execute();
    }

    public static <T> DataStream<T> createDataStream(StreamExecutionEnvironment env,
                                                     T... data) {
        return env.fromCollection(Arrays.asList(data))
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<T>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<T>() {
                                    @Override
                                    public long extractTimestamp(T element, long recordTimestamp) {
                                        if (element.getClass() == Tuple3.class) {
                                            if (((Tuple3<?, ?, ?>) element).f2.getClass() == Long.class) {
                                                return ((Tuple3<String, String, Long>) element).f2;
                                            } else {
                                                throw new IllegalArgumentException("类型错误！");
                                            }
                                        } else {
                                            // 报错
                                            throw new IllegalArgumentException("类型错误！");
                                        }
                                    }
                                })
                );
    }

}
