package com.wzq.ch8;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 实时对账，app的支付操作和第三方的支付操作的一个双流
 * App的支付事件和第三方的支付事件将会互相等到5秒钟，如果等不来对应的支付事件，那么就输出报警信息
 *
 * @author wzq
 * @create 2022-10-04 17:17
 */
public class BillCheckExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 来自app的支付日志
        SingleOutputStreamOperator<Tuple3<String, String, Long>> appStream = env.fromElements(
                Tuple3.of("order-1", "app", 1000L),
                Tuple3.of("order-2", "app", 2000L)
        ).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Tuple3<String, String, Long>>forMonotonousTimestamps()
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple3<String, String, Long> element, long recordTimestamp) {
                                return element.f2;
                            }
                        })
        );

        // 来自第三方支付平台的支付日志
        SingleOutputStreamOperator<Tuple4<String, String, String, Long>> thirdpartyStream = env.fromElements(
                Tuple4.of("order-1", "third-party", "success", 3000L),
                Tuple4.of("order-3", "third-party", "success", 4000L)
        ).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Tuple4<String, String, String, Long>>forMonotonousTimestamps()
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple4<String, String, String, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple4<String, String, String, Long> element, long recordTimestamp) {
                                return element.f3;
                            }
                        })
        );

        // 检查同一支付订单在两条流中是否匹配，不匹配就报警
        appStream.connect(thirdpartyStream)
                // 可以直接使用keyBy，会将相同的数据放到一起，注意：key的数据类型要保持一致
                .keyBy(data -> data.f0, data -> data.f0)
                .process(new OrderMatchResult())
                .print();
        env.execute();
    }

    // 自定义实现CoProcessFunction
    public static class OrderMatchResult extends CoProcessFunction<Tuple3<String, String, Long>,
            Tuple4<String, String, String, Long>, String> {

        // 定义状态变量，用来保存已经到达的事件
        private ValueState<Tuple3<String, String, Long>> appEventState;
        private ValueState<Tuple4<String, String, String, Long>> thirtpartyEventState;

        @Override
        public void open(Configuration parameters) throws Exception {
            appEventState = getRuntimeContext().getState(
                    new ValueStateDescriptor<Tuple3<String, String, Long>>(
                            "app-event",
                            Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)
                    )
            );

            thirtpartyEventState = getRuntimeContext().getState(
                    new ValueStateDescriptor<Tuple4<String, String, String, Long>>(
                            "thirdparty-event",
                            Types.TUPLE(Types.STRING, Types.STRING, Types.STRING, Types.LONG)
                    )
            );
        }

        @Override
        public void processElement1(Tuple3<String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
            // 看另一条流中事件是否来过
            if (thirtpartyEventState.value() != null) {
                out.collect("对账成功：" + value + " " + thirtpartyEventState.value());
                // 情况状态
                thirtpartyEventState.clear();
            } else {
                // 更新状态
                appEventState.update(value);
                // 注册一个5秒后的定时器，开始等待另一条流的事件
                ctx.timerService().registerEventTimeTimer(value.f2 + 5000L);
            }
        }

        @Override
        public void processElement2(Tuple4<String, String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
            // 看另一条流中事件是否来过
            if (appEventState.value() != null) {
                out.collect("对账成功：" + value + " " + appEventState.value());
                // 情况状态
                appEventState.clear();
            } else {
                // 更新状态
                thirtpartyEventState.update(value);
                // 注册一个5秒后的定时器，开始等待另一条流的事件
                ctx.timerService().registerEventTimeTimer(value.f3 + 5000L);
            }
        }

        @Override
        public void onTimer(long timestamp, CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            // 定时器触发，如果某个状态不为空，说明另一条流中事件没来
            if (appEventState.value() != null) {
                out.collect("对账失败：" + appEventState.value() + " 第三方信息未到");
            }
            if (thirtpartyEventState.value() != null) {
                out.collect("对账失败：" + thirtpartyEventState.value() + " app信息未到");
            }
            appEventState.clear();
            thirtpartyEventState.clear();
        }
    }

}