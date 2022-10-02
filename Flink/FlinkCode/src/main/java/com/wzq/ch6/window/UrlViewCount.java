package com.wzq.ch6.window;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wzq
 * @create 2022-10-02 21:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlViewCount {

    public String url;
    public Long count;
    public Long windowStart;
    public Long windowEnd;

}
