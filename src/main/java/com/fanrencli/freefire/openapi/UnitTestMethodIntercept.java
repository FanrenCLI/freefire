package com.fanrencli.freefire.openapi;

public interface UnitTestMethodIntercept {
    /**
     * 单元测试方法拦截
     */
    boolean isInterceptedMehod(String className, String methodName);
    /**
     * 单元测试代码生成路径
     */
    String getUnitTestCodeFilePath();
}
