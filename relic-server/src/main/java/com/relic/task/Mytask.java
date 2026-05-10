package com.relic.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j

public class Mytask {

    @Scheduled(cron = "0/5 * * * * ?")
    public void task1(){
//        log.info("task1开始执行:{}",new Date());
    }
}
