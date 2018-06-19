package com.job;

import java.io.IOException;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.data.Oracle2Redis;
import com.logger.Logger;

public class Detail implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger.logger.info("定时任务开始！");

        // 定义Oracle2Redis
        Oracle2Redis o2R = new Oracle2Redis();

        // 获取Properties数据
        Properties properties = new Properties();
        try {
            properties.load(Oracle2Redis.class.getResourceAsStream("Redis.properties"));

            Logger.logger.info("成功加载Redis.properties配置文件。");

            // 循环更新Redis
            for (int i = 0; i < properties.size(); i++) {
                o2R.addKey(i);
            }
        } catch (IOException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        Logger.logger.info("定时任务结束！");
    }
}
