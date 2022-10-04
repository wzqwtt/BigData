package com.wzq.ch7;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 非常经典的例子：实时统计一段时间内的热门url。
 * 例如：需要统计最近10秒内最热门的两个url链接，并且5秒更新一次
 *
 * @author wzq
 * @create 2022-10-04 13:53
 */
public class TopNProcessAllWindowFunction {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 添加输入源，并且设置水位线
        SingleOutputStreamOperator<Event> eventStream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Event>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                                    @Override
                                    public long extractTimestamp(Event element, long recordTimestamp) {
                                        return element.timestamp;
                                    }
                                })
                );

        SingleOutputStreamOperator<String> result = eventStream
                // 根据业务需要，只需要url即可
                .map(e -> e.url)
                // 开滑动窗口
                .windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(ProcessAllWindowFunction<String, String, TimeWindow>.Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        // 遍历窗口中的数据，将浏览量保存到map中
                        HashMap<String, Long> urlCountMap = new HashMap<>();
                        for (String url : elements) {
                            urlCountMap.put(url, urlCountMap.getOrDefault(url, 0L) + 1L);
                        }

                        // 将浏览量数据放入ArrayList
                        ArrayList<Tuple2<String, Long>> mapList = new ArrayList<>();
                        for (String url : urlCountMap.keySet()) {
                            mapList.add(Tuple2.of(url, urlCountMap.get(url)));
                        }

                        // ArrayList排序
                        mapList.sort(new Comparator<Tuple2<String, Long>>() {
                            @Override
                            public int compare(Tuple2<String, Long> o1, Tuple2<String, Long> o2) {
                                return o2.f1.intValue() - o1.f1.intValue();
                            }
                        });

                        // 取出前两名，构建输出结果
                        StringBuilder result = new StringBuilder();
                        for (int i = 0; i < 2; i++) {
                            Tuple2<String, Long> tmp = mapList.get(i);
                            String info = "浏览量 No." + (i + 1) +
                                    "url: " + tmp.f0 +
                                    "浏览量: " + tmp.f1 +
                                    " 窗口结束时间：" + new Timestamp(context.window().getEnd()) + "\n";
                            result.append(info);
                        }
                        out.collect(result.toString());
                    }
                });

        result.print();

        env.execute();
    }

}
