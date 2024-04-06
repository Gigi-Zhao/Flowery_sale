package com.flow.context;

// 在同一线程中存储公共变量
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
