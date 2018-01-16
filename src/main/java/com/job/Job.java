package com.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.data.SelectHzGisTpsFw;

public class Job {
	/**
	 * 定时任务
	 */
	public static void start() {

		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// define the jobDetail and tie it to our SelectHzGisTpsFw class
		JobDetail jobDetail = JobBuilder.newJob(SelectHzGisTpsFw.class).withIdentity("jobDetail", "group").build();

		// Trigger the job to run now, and then repeat every 6 hours
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "group").startNow()
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 0/6 * * ?")).build();

		// Tell quartz to schedule the jobDetail using our trigger
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// and start it off
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
