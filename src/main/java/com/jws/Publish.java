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

		String address = "http://172.16.2.111/Jws";
		Object implementor = new Jws();
		Endpoint.publish(address, implementor);
		logger.info(address + "?wsdl");
		logger.info("wsimport -keep " + address + "?wsdl");
	}
}