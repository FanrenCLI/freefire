package com.fanrencli.freefire.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring ApplicationContext 工具类
 */
@Component
public class SpringBeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        if(applicationContext.containsBean(beanName)){
            return (T) applicationContext.getBean(beanName);
        }else{
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> baseType){
        return applicationContext.getBeansOfType(baseType);
    }
}
