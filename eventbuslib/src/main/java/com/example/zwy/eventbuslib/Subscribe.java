package com.example.zwy.eventbuslib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zhen Weiyu on 2017/5/9.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    ThreadMode threadMode() default ThreadMode.PostThread;

    boolean sticky() default false;
}
