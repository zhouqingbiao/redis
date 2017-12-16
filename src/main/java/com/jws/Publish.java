package com.jws;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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

		String address = null;
		try {
			// 获取IP地址
			address = Inet4Address.getLocalHost().getHostAddress();

			// 组装WebService地址
			address = "http://" + address + "/Jws";

			// 发布WebService
			Object implementor = new Jws();
			Endpoint.publish(address, implementor);

			// 输出WebService地址及如何解析操作
			logger.info(address + "?wsdl");
			logger.info("wsimport -keep " + address + "?wsdl");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
		}

	}
}