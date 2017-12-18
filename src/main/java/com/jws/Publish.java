package com.jws;

import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import com.job.Job;
import com.log.Reggol;

public class Publish {

	// 获得Logger
	static Logger logger = Reggol.getLogger();

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