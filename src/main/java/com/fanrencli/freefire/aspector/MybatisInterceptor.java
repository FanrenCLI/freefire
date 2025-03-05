package com.fanrencli.freefire.aspector;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fanrencli.freefire.entity.InvokeEntity;
import com.fanrencli.freefire.utils.MethodUnitTestConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MybatisInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (null == MethodUnitTestConstants.isInterceptedMethod()) return invocation.proceed();
        InvokeEntity invokeEntity = handleInvokeEntity(invocation);
        Object proceed = invocation.proceed();
        invokeEntity.setResponseParamJsonString(JSON.toJSONString(proceed, SerializerFeature.IgnoreErrorGetter));
        log.info("方法调用信息：{}", JSON.toJSONString(invokeEntity, SerializerFeature.IgnoreErrorGetter));
        MethodUnitTestConstants.getThreadLocalList().add(invokeEntity);
        this.buildInvokeEntityTree(invokeEntity);
        return null;
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

    private InvokeEntity handleInvokeEntity(Invocation invocation) {
        InvokeEntity invokeEntity = new InvokeEntity();
        MappedStatement mappedStatement;
        Object[] args = invocation.getArgs();
        mappedStatement = (MappedStatement) args[0];
        invokeEntity.setRequestParamJsonString(JSON.toJSONString(args[1]));
        String className = mappedStatement.getId().substring(0,mappedStatement.getId().lastIndexOf("."));
        String methodName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".")+1);
        invokeEntity.setClassName(className);
        invokeEntity.setMethodName(methodName);
        invokeEntity.setInvokeDeepLevel(MethodUnitTestConstants.getInvokeDeepLevel());
        return invokeEntity;
    }
}
