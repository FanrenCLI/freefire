package com.fanrencli.freefire.schedual;

import com.fanrencli.freefire.service.IUnitTestFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedualTask {
    @Autowired
    private IUnitTestFileService unitTestFileService;
    @Scheduled(fixedDelay = 3000)
    public void genFileTask() throws InterruptedException{
        Thread.sleep(3000);
        unitTestFileService.generateTestFile();
    }
}
