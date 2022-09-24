package com.wzq.ch5.transorm;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/**
 * 物理分区 —— rescale()
 * <p>
 * 底层Round-Roin，不同于Reblanace的是，多了很多发牌人
 *
 * @author wzq
 * @create 2022-09-24 20:26
 */
public class RescaleDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.addSource(new RichParallelSourceFunction<Integer>() {
                    @Override
                    public void run(SourceContext<Integer> ctx) throws Exception {
                        for (int i = 0; i < 8; i++) {
                            // 将奇数发送到索引为1的并行子任务
                            // 将偶数发送到索引为0的并行子任务

                            if ((i + 1) % 2 == getRuntimeContext().getIndexOfThisSubtask()) {
                                ctx.collect(i + 1);
                            }
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                }).setParallelism(2)
                .rescale()
                .print()
                .setParallelism(4);

        env.execute();
    }

}
