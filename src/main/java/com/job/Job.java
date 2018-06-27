package com.job;

import com.logger.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Job {

    /**
     * 定时任务
     */
    public static void start() {

        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        // 建立JobDetail
        JobDetail job = JobBuilder.newJob(Detail.class).withIdentity("job", "group").build();

        // 建立Trigger
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "group").startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0/6 * * ?")).build();

        // Tell quartz to schedule the job using our trigger
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        // and start it off
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            Logger.logger.warn(e.getMessage(), e);
        }
    }
}
