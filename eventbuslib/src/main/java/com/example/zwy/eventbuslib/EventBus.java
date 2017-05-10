package com.example.zwy.eventbuslib;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Zhen Weiyu on 2017/5/9.
 */

public class EventBus {

    private static volatile  EventBus instance = null;

    private ConcurrentHashMap<Object,List<SubscribeMethod>> mSubscribeHashMap;

    private ConcurrentHashMap<Class<?>,Object> stickyEvents;

    private ExecutorService executorService;

    private EventBus(){
         mSubscribeHashMap = new ConcurrentHashMap<>();
         stickyEvents = new ConcurrentHashMap<>();
         executorService = Executors.newFixedThreadPool(10);
    }

    public static EventBus getDefault(){
        if( instance == null){
            synchronized (EventBus.class){
                if(instance == null){
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object object){
         if(mSubscribeHashMap!=null){
             if(mSubscribeHashMap.get(object)==null){
                List<SubscribeMethod> list = findSubscribeMethod(object);
                mSubscribeHashMap.put(object,list);
             }


         }
    }

    public void unregister(Object object){
        if(mSubscribeHashMap!=null){
            if(mSubscribeHashMap.containsKey(object)){
                mSubscribeHashMap.remove(object);
            }
        }
    }


    public void post(final Object param){
        Set<Object> keySet = mSubscribeHashMap.keySet();
        Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()){
            final Object activity = iterator.next();
            List<SubscribeMethod> list = mSubscribeHashMap.get(activity);
            for(final SubscribeMethod subscribeMethod:list){
                if(subscribeMethod.getEventType().isAssignableFrom(param.getClass())){
                    switch (subscribeMethod.getThreadMode()) {
                        case BackgroundThread:
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        subscribeMethod.getMethod().invoke(activity,param);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case MainThread:
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                try {
                                    subscribeMethod.getMethod().invoke(activity,param);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            subscribeMethod.getMethod().invoke(activity,param);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        case PostThread:
                            try {
                                subscribeMethod.getMethod().invoke(activity,param);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                    }

                }
            }
        }
    }

    public void postSticky(Object object){
        synchronized (stickyEvents){
            stickyEvents.put(object.getClass(),object);
        }
        post(object);

    }



    private List<SubscribeMethod> findSubscribeMethod(Object object) {
        List<SubscribeMethod> list = new CopyOnWriteArrayList<>();
        Class<?> mClass = object.getClass();
        while (mClass!=null){
            String name = mClass.getName();
            if(name.startsWith("java.")||name.startsWith("javax.")||name.startsWith("android.")){
                break;
            }
            for(Method method:mClass.getMethods()){
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
               if(subscribe!=null){
                   if(method.getParameterTypes().length!=1){
                       try {
                           throw new Exception("one more parameter is error!");
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                   Class<?> parameterClass = method.getParameterTypes()[0];
                   ThreadMode threadMode = subscribe.threadMode();
                   SubscribeMethod subscribeMethod = new SubscribeMethod(method,threadMode,parameterClass);
                   list.add(subscribeMethod);
                   if(subscribe.sticky()){
                       if(stickyEvents.get(parameterClass)!=null){
                           Object param = stickyEvents.get(parameterClass);
                           try {
                               subscribeMethod.getMethod().invoke(object,param);
                           } catch (IllegalAccessException e) {
                               e.printStackTrace();
                           } catch (InvocationTargetException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }
            }
            mClass = mClass.getSuperclass();
        }
        return list;
    }







}
