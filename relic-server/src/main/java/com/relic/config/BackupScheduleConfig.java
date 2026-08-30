package com.relic.config;

import com.relic.mapper.BackupStrategyMapper;
import com.relic.task.BackupScheduledTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;

/**
 * 自动备份任务的动态调度配置。
 *
 * <p>执行周期来源于 backup_strategies.backup_cron，每次触发前重新读取策略表，
 * 管理员修改备份时间后无需重启应用即可生效；cron 缺失或非法时回退为每天 03:00。</p>
 */
@Configuration
@Slf4j
public class BackupScheduleConfig implements SchedulingConfigurer {

    /** 策略未配置或配置非法时的兜底执行时间：每天 03:00 */
    private static final String DEFAULT_CRON = "0 0 3 * * ?";

    private final BackupScheduledTask backupScheduledTask;
    private final BackupStrategyMapper backupStrategyMapper;

    public BackupScheduleConfig(BackupScheduledTask backupScheduledTask, BackupStrategyMapper backupStrategyMapper) {
        this.backupScheduledTask = backupScheduledTask;
        this.backupStrategyMapper = backupStrategyMapper;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(backupScheduledTask::autoBackup, nextExecutionTrigger());
    }

    private Trigger nextExecutionTrigger() {
        return triggerContext -> {
            String cron = readCronFromStrategy();
            try {
                return new CronTrigger(cron).nextExecution(triggerContext);
            } catch (IllegalArgumentException e) {
                log.warn("备份 cron 配置非法：{}，回退为默认 {}。", cron, DEFAULT_CRON);
                return new CronTrigger(DEFAULT_CRON).nextExecution(triggerContext);
            }
        };
    }

    private String readCronFromStrategy() {
        try {
            Map<String, Object> strategy = backupStrategyMapper.selectCurrent();
            if (strategy != null && strategy.get("backupCron") != null) {
                String cron = strategy.get("backupCron").toString().trim();
                if (!cron.isEmpty()) {
                    return cron;
                }
            }
        } catch (Exception e) {
            log.warn("读取备份策略 cron 失败，使用默认执行时间：{}", e.getMessage());
        }
        return DEFAULT_CRON;
    }
}
