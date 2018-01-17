package com.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Job {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	/**
	 * 定时任务
	 */
	public static void start() {

		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.warn(e);
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
			e.printStackTrace();
			logger.warn(e);
		}

		// and start it off
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.warn(e);
		}
	}
}
