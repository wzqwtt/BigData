package com.wzq.ch5.transorm;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * RichFunction：富函数类，比常规类提供更多、更丰富的功能。
 * <p>
 * 富函数类可以获取程序运行的上下文，并拥有一些生命周期方法：
 * <p>
 * - open()
 * - close()
 *
 * @author wzq
 * @create 2022-09-24 20:09
 */
public class RichFunctionDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<Event> source = env.fromElements(
                new Event("wtt", "./home", 1000L),
                new Event("wzq", "./card", 2000L),
                new Event("wttwzq", "./index", 3000L),
                new Event("wzqwtt", "./prod?id-1", 4000L)
        );

        source.map(new RichMapFunction<Event, Long>() {

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                System.out.println("索引为" + getRuntimeContext().getIndexOfThisSubtask() + "的任务开始了");
            }

            @Override
            public Long map(Event value) throws Exception {
                return value.timestamp;
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("索引为" + getRuntimeContext().getIndexOfThisSubtask() + "的任务结束了");
            }
        }).print();

        env.execute();
    }

}
