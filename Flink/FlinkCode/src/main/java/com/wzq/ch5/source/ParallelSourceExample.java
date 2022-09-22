package com.wzq.ch5.source;

import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 自定义并行数据源
 *
 * @author wzq
 * @create 2022-09-18 22:16
 */
public class ParallelSourceExample implements ParallelSourceFunction<Integer> {

    private Boolean running = true;

    @Override
    public void run(SourceContext<Integer> ctx) throws Exception {
        Random random = new Random();
        while (running) {
            ctx.collect(random.nextInt());
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
