package com.wzq.ch6.watermark;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

/**
 * @author wzq
 * @create 2022-09-27 20:30
 */
public class WaterMarkAPIDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        // 策略1：WatermarkStrategy.noWatermarks() 不生成watermark，禁用事件时间推进机制
        stream.assignTimestampsAndWatermarks(WatermarkStrategy.noWatermarks());

        // 策略2：WatermarkStrategy.forMonotonousTimestamps() 紧跟最大事件时间
        stream.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        // 策略3：WatermarkStrategy.forBoundedOutOfOrderness() 允许乱序的Watermark生成策略
        stream.assignTimestampsAndWatermarks(
                // 构造一个watermark的生成策略（算法的策略、事件时间抽取方法）
                WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofMillis(2000))
                        // 指定数据源中哪个字段作为事件的时间
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        })
        );

        // 策略4：WatermarkStrategy.forGenerator() 自定义watermark生成算法
        // stream.assignTimestampsAndWatermarks(WatermarkStrategy.forGenerator());

        env.execute();
    }

}
