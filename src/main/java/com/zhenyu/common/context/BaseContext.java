package com.zhenyu.common.context;


/**
 * 线程上下文工具类
 * 用于存储当前线程的用户ID（线程隔离）
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {

        threadLocal.set(id);
    }

    public static Long getCurrentId() {

        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
