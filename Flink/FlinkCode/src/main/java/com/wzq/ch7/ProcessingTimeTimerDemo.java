package com.wzq.ch7;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;

/**
 * 基于KeyedStream，处理时间定时器Demo
 *
 * @author wzq
 * @create 2022-10-03 21:00
 */
public class ProcessingTimeTimerDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 以处理时间为准，不需要额外设置水位线
        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        // 要用定时器，必须基于KeyedStream
        stream.keyBy(data -> true)
                .process(new KeyedProcessFunction<Boolean, Event, String>() {
                    /**
                     * 每来一条数据都会调用一次，在这里主要定义了一个十秒以后的定时器
                     */
                    @Override
                    public void processElement(Event value, KeyedProcessFunction<Boolean, Event, String>.Context ctx, Collector<String> out) throws Exception {
                        long currTs = ctx.timerService().currentProcessingTime();
                        out.collect("数据到达，到达时间：" + new Timestamp(currTs));
                        // 注册一个10秒以后的定时器
                        ctx.timerService().registerProcessingTimeTimer(currTs / 1000 * 1000 + 10 * 1000L);
                    }

                    /**
                     * 该方法会在定时器触发时候回调
                     */
                    @Override
                    public void onTimer(long timestamp, KeyedProcessFunction<Boolean, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                        out.collect("定时器触发，触发时间：" + new Timestamp(timestamp));
                    }
                }).print();

        env.execute();
    }

}
