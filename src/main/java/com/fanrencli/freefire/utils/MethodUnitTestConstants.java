package com.fanrencli.freefire.utils;

import com.fanrencli.freefire.entity.InvokeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MethodUnitTestConstants {
    // 保存所有方法的调用树
    public static LinkedBlockingQueue<InvokeEntity> invokeEntityList = new LinkedBlockingQueue<>(100);
    // 判断方法是否应该被拦截
    private static Map<String,Boolean> interceptedMethodList;
    public static void addInterceptedMethod(){
        if (null == threadLocalInvokeDeepLevel) interceptedMethodList = new ConcurrentHashMap<>();
        interceptedMethodList.put(Thread.currentThread().getName(),true);
    }
    public static void removeInterceptedMethod(){
        if (null == threadLocalInvokeDeepLevel) interceptedMethodList.remove(Thread.currentThread().getName());
        interceptedMethodList.remove(Thread.currentThread().getName());
    }
    public static Boolean isInterceptedMethod(){
        if (null == threadLocalInvokeDeepLevel) interceptedMethodList = new ConcurrentHashMap<>();
        return interceptedMethodList.get(Thread.currentThread().getName());
    }
    /**
     * 保存调用树所有对象
     */
    private static final ThreadLocal<List<InvokeEntity>> threadLocal = new ThreadLocal<>();
    public static List<InvokeEntity> getThreadLocalList(){
        if (null == MethodUnitTestConstants.threadLocal.get()) MethodUnitTestConstants.threadLocal.set(new ArrayList<>());
        return threadLocal.get();
    }
    public static void removeThreadLocalList(){
        if (threadLocal.get()!=null) threadLocal.remove();
    }
    /**
     * 记录线程当前调用深度，key:threadName,value:deepLevel
     */
    private static Map<String,Integer> threadLocalInvokeDeepLevel;
    public static Integer getInvokeDeepLevel(){
        if (null == threadLocalInvokeDeepLevel) threadLocalInvokeDeepLevel = new ConcurrentHashMap<>();
        threadLocalInvokeDeepLevel.put(Thread.currentThread().getName(),threadLocalInvokeDeepLevel.getOrDefault(Thread.currentThread().getName(),0));
        return threadLocalInvokeDeepLevel.get(Thread.currentThread().getName());
    }
    public static void removeInvokeDeepLevel(){
        if (null != threadLocalInvokeDeepLevel.get(Thread.currentThread().getName())) {
            threadLocalInvokeDeepLevel.remove(Thread.currentThread().getName());
        }
    }
    public static void addInvokeDeepLevel(){
        if (null == threadLocalInvokeDeepLevel) threadLocalInvokeDeepLevel = new ConcurrentHashMap<>();
        threadLocalInvokeDeepLevel.put(Thread.currentThread().getName(),threadLocalInvokeDeepLevel.getOrDefault(Thread.currentThread().getName(),0)+1);
    }
    public static void decrementInvokeDeepLevel(){
        if (null == threadLocalInvokeDeepLevel) threadLocalInvokeDeepLevel = new ConcurrentHashMap<>();
        threadLocalInvokeDeepLevel.put(Thread.currentThread().getName(),threadLocalInvokeDeepLevel.getOrDefault(Thread.currentThread().getName(),0)-1);
    }
}
