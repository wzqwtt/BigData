package com.wzq.ch5.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Calendar;
import java.util.Random;

/**
 * 自定义Source
 *
 * @author wzq
 * @create 2022-09-18 22:09
 */
public class ClickSource implements SourceFunction<Event> {

    // 生命一个布尔变量，作为控制数据生成的标志位
    private Boolean running = true;

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {
        // 在指定数据集中随机选取数据
        Random random = new Random();

        String[] users = {"wzq", "wtt", "wzqwtt", "car"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=1", "./prod?id=2"};

        while (running) {
            ctx.collect(new Event(
                    users[random.nextInt(users.length)],
                    urls[random.nextInt(urls.length)],
                    Calendar.getInstance().getTimeInMillis()
            ));
            // 隔一秒生成一个点击事件，方便观测
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
