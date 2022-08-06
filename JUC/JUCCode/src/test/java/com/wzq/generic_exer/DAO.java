package com.wzq.generic_exer;

import java.util.List;

/**
 * DAO：Data(base) access object
 *
 * @author wzq
 * @create 2022-08-06 17:09
 */
public class DAO<T> {   // 操作表的共性操作的DAO

    // 添加一条记录
    public void add(T t) {

    }

    // 删除一条记录
    public boolean remove(int index) {
        return false;
    }

    // 修改一条记录
    public void update(int index, T t) {

    }

    // 查询一条记录
    public T getIndex(int index) {
        return null;
    }

    // 查询多条记录
    public List<T> getForList(int index) {
        return null;
    }

    // 泛型方法
    // 举例：获取表中一共有多少条记录？获取最大的员工入职时间
    public <E> E getValue() {
        return null;
    }
}
