package com.fanrencli.freefire.aspector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fanrencli.freefire.entity.InvokeEntity;
import com.fanrencli.freefire.openapi.UnitTestMethodIntercept;
import com.fanrencli.freefire.utils.MethodUnitTestConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Aspect
@Component
@Service
@Slf4j
public class MehtodInvokeAspect {

    @Autowired(required = false)
    private UnitTestMethodIntercept unitTestMethodIntercept;

    /**
     * 获取方法的入参和出参
     */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if ( null != unitTestMethodIntercept && null == MethodUnitTestConstants.isInterceptedMethod()) {
            if (unitTestMethodIntercept.isInterceptedMehod(joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName())){
                MethodUnitTestConstants.addInterceptedMethod();
            }else{
                return joinPoint.proceed();
            }
        }
        // 创建变量记录方法入参，出参，类名，方法名，子调用列表
        InvokeEntity invokeEntity = new InvokeEntity();
        MethodUnitTestConstants.getThreadLocalList().add(invokeEntity);
        invokeEntity.setMethodName(joinPoint.getSignature().getName());
        invokeEntity.setClassName(joinPoint.getTarget().getClass().getName());
        invokeEntity.setRequestParamJsonString(JSON.toJSONString(joinPoint.getArgs(), SerializerFeature.IgnoreErrorGetter));
        invokeEntity.setInvokeDeepLevel(MethodUnitTestConstants.getInvokeDeepLevel());
        // 调用层级+1
        MethodUnitTestConstants.addInvokeDeepLevel();
        // 执行目标方法
        Object proceed = joinPoint.proceed();
        invokeEntity.setResponseParamJsonString(JSON.toJSONString(proceed, SerializerFeature.IgnoreErrorGetter));
        // 调用层级-1
        MethodUnitTestConstants.decrementInvokeDeepLevel();
        log.info("方法调用信息：{}", JSON.toJSONString(invokeEntity, SerializerFeature.IgnoreErrorGetter));
        // 构建方法调用树
        this.buildInvokeEntityTree(invokeEntity);
        if (MethodUnitTestConstants.getInvokeDeepLevel() ==0 ){
            this.saveInvokeEntity();
            MethodUnitTestConstants.removeThreadLocalList();
            MethodUnitTestConstants.removeInterceptedMethod();
            MethodUnitTestConstants.removeInvokeDeepLevel();
        }
        return joinPoint.proceed();
    }

    private void saveInvokeEntity() {
        for (InvokeEntity invokeEntity : MethodUnitTestConstants.getThreadLocalList()) {
            if (null != unitTestMethodIntercept && unitTestMethodIntercept.isInterceptedMehod(invokeEntity.getClassName(), invokeEntity.getMethodName())){
                if (MethodUnitTestConstants.invokeEntityList.size() == 100) MethodUnitTestConstants.invokeEntityList.poll();
                MethodUnitTestConstants.invokeEntityList.offer(invokeEntity);
            }
        }

    }

    private void buildInvokeEntityTree(InvokeEntity invokeEntity) {
        List<InvokeEntity> threadLocal = MethodUnitTestConstants.getThreadLocalList();
        for(int i=threadLocal.size()-1;i>=0;i--){
            if(threadLocal.get(i).getInvokeDeepLevel() == invokeEntity.getInvokeDeepLevel()-1){
                threadLocal.get(i).getChildren().add(invokeEntity);
                break;
            }
        }
    }
}
