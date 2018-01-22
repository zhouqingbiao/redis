package com.job;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.data.Oracle2Redis;

public class Detail implements Job {
	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("定时任务开始！");

		// 定义Oracle2Redis
		Oracle2Redis o2R = new Oracle2Redis();

		// 获取Properties数据
		Properties properties = new Properties();
		try {
			properties.load(Oracle2Redis.class.getResourceAsStream("Redis.properties"));
			logger.info("成功加载Redis.properties配置文件");

			// 循环更新Redis
			for (int i = 0; i < properties.size(); i++) {
				o2R.addKey(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn(e);
		}
		logger.info("定时任务结束！");
	}
}
