package com.wzq.ch7;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 侧输出流
 *
 * @author wzq
 * @create 2022-10-04 14:44
 */
public class SideOutputDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        OutputTag<String> outputTag = new OutputTag<String>("side-output") {
        };
        SingleOutputStreamOperator<String> stream = env.addSource(new ClickSource())
                .process(new ProcessFunction<Event, String>() {
                    @Override
                    public void processElement(Event value, ProcessFunction<Event, String>.Context ctx, Collector<String> out) throws Exception {
                        // 输出user到主流
                        out.collect(value.user);
                        // 输出url到支流
                        ctx.output(outputTag, "side-output: " + value.url);
                    }
                });

        DataStream<String> sideOutput = stream.getSideOutput(outputTag);
        sideOutput.print();

        env.execute();
    }

}
