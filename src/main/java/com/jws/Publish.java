package com.jws;

import javax.xml.ws.Endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.job.Job;

/**
 * 定时任务启动并发布WebService
 * 
 * @author QB.Zhou
 *
 */
public class Publish {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public static void main(String[] args) {

		// 启动定时任务0 0 0/6 * * ?
		Job.start();

		// IP地址
		String address = "172.16.100.51";

		// 组装WebService地址
		address = "http://" + address + "/Jws";

		// 发布WebService
		Object implementor = new Jws();
		Endpoint.publish(address, implementor);

		// 输出WebService地址及如何解析操作
		logger.info(address + "?wsdl");
		logger.info("wsimport -keep " + address + "?wsdl");

	}
}