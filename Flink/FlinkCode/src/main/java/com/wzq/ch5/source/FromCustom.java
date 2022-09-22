package com.wzq.ch5.source;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 测试自定义数据源
 *
 * @author wzq
 * @create 2022-09-18 22:14
 */
public class FromCustom {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 实现SourceFunction接口，并行度只能为1
        env.addSource(new ClickSource()).print("SourceCustom");

        // 测试并行数据源，ParallelSourceFunction
//        env.addSource(new ParallelSourceExample()).setParallelism(4).print("ParallelSource");

        env.execute();
    }

}
