package com.wzq.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class JSONUtils {
    // 判断是否为JSON格式文件
    public static boolean isJSONValidate(String log) {
        try {
            JSON.parse(log);    // 如果解析过来不报异常就是JSON格式文件
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
