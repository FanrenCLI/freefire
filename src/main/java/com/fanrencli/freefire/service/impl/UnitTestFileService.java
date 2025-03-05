package com.fanrencli.freefire.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fanrencli.freefire.entity.InvokeEntity;
import com.fanrencli.freefire.service.IUnitTestFileService;
import com.fanrencli.freefire.utils.MethodUnitTestConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class UnitTestFileService implements IUnitTestFileService {
    @Override
    @Async("genUnitTestFileThreadPool")
    @SneakyThrows
    public void generateTestFile() {
        while(MethodUnitTestConstants.invokeEntityList.size()>0){
            InvokeEntity poll = MethodUnitTestConstants.invokeEntityList.poll();
            this.handleInvokeEntity(poll);
        }
    }

    @SneakyThrows
    private void handleInvokeEntity(InvokeEntity poll) {
        for (InvokeEntity child : poll.getChildren()) {
            child.setChildren(null);
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(poll.getMethodName() + "-" + UUID.randomUUID() + ".json")), StandardCharsets.UTF_8);
        outputStreamWriter.write(JSON.toJSONString(poll, SerializerFeature.IgnoreErrorGetter,SerializerFeature.PrettyFormat));
        outputStreamWriter.flush();
        outputStreamWriter.close();
        log.info(">>>>>>>>>>>>>>>>>>>>>生成单元测试文件：{}<<<<<<<<<<<<<<<<<<<<<<<<<",poll.getMethodName() + "-" + UUID.randomUUID() + ".json");

    }
}
