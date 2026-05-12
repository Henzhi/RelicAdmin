package com.relic.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static ThreadLocal<String> userTypeThreadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentUserType(String userType) {
        userTypeThreadLocal.set(userType);
    }

    public static String getCurrentUserType() {
        return userTypeThreadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
        userTypeThreadLocal.remove();
    }

}
