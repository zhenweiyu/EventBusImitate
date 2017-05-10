package com.example.zwy.eventbuslib;

import java.lang.reflect.Method;

/**
 * Created by Zhen Weiyu on 2017/5/9.
 */

public class SubscribeMethod {

   private ThreadMode threadMode;

   private Method method;

   private Class<?>  eventType;

    public SubscribeMethod(Method method,ThreadMode threadMode,Class<?> eventType){
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
