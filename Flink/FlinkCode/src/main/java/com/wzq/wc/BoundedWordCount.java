package com.wzq.wc;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 有界流
 *
 * @author wzq
 * @create 2022-08-15 16:28
 */
public class BoundedWordCount {

    public static void main(String[] args) throws Exception {
        // 1、创建流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2、读取文件
        DataStreamSource<String> linesDataSreamSource = env.readTextFile("input/words.txt");

        // 3、扁平化操作
        SingleOutputStreamOperator<Tuple2<String, Long>> wordAndOneTuple =
                linesDataSreamSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        out.collect(Tuple2.of(word, 1L));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        // 4、groupBy
        KeyedStream<Tuple2<String, Long>, String> wordAndOneKeyStream = wordAndOneTuple.keyBy(obj -> obj.f0);

        // 5、求和
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = wordAndOneKeyStream.sum(1);

        // 6、输出结果
        sum.print();

        // 7、执行
        env.execute();
    }

}
