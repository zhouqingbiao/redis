package com.jws;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import com.job.Job;
import com.log.Reggol;

public class Publish {

	// ���Logger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {

		// ������ʱ����0 0 0/6 * * ?
		Job.start();

		String address = null;
		try {
			// ��ȡIP��ַ
			address = Inet4Address.getLocalHost().getHostAddress();

			// ��װWebService��ַ
			address = "http://" + address + "/Jws";

			// ����WebService
			Object implementor = new Jws();
			Endpoint.publish(address, implementor);

			// ���WebService��ַ����ν�������
			logger.info(address + "?wsdl");
			logger.info("wsimport -keep " + address + "?wsdl");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}