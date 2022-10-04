package com.wzq.ch8;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;

/**
 * 两条流连接connect
 *
 * @author wzq
 * @create 2022-10-04 16:59
 */
public class CoMapExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 两条流的数据类型可以不同
        DataStream<Integer> intStream = env.fromElements(1, 2, 3);
        DataStream<Long> longStream = env.fromElements(1L, 2L, 3L);

        // 两条流合并之后是ConnectedStreams，两个泛型参数分别对应两条流的数据类型
        ConnectedStreams<Integer, Long> connectedStreams = intStream.connect(longStream);

        // 进行同处理操作转换为一条DataStream
        // 这里需要填写3个泛型，分别对应：第一条流数据类型；第二条流数据类型；连接之后的数据类型
        SingleOutputStreamOperator<String> result = connectedStreams.map(new CoMapFunction<Integer, Long, String>() {
            // map1方法，处理第一条数据流，返回连接之后的数据类型
            @Override
            public String map1(Integer value) throws Exception {
                return "Integer: " + value;
            }

            // map2方法，处理第二条数据流，返回连接之后的数据类型
            @Override
            public String map2(Long value) throws Exception {
                return "Long: " + value;
            }
        });

        result.print();

        env.execute();
    }

}
