package com.gettimhired.config;

public class RequestContextHolder {

    private static final ThreadLocal<String> authorization = new ThreadLocal<>();

    public static void setHeader(String headerValue) {
        authorization.set(headerValue);
    }

    public static String getHeader() {
        return authorization.get();
    }

    public static void clear() {
        authorization.remove();
    }
}
