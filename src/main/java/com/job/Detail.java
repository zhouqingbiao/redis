package com.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.data.SelectHzFwdjTpfJcdjb;
import com.data.SelectHzGisTpsFw;
import com.data.SelectHzGisTpsZrz;

public class Detail implements Job {
	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("定时任务开始！");
		new SelectHzFwdjTpfJcdjb().addKey();
		new SelectHzGisTpsFw().addKey();
		new SelectHzGisTpsZrz().addKey();
		logger.info("定时任务结束！");
	}
}
