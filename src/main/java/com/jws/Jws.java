package com.jws;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.data.Oracle2Redis;
import com.data.Redis2WebService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.internal.ws.developer.JAXWSProperties;

@WebService
public class Jws {
	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	@Resource
	private WebServiceContext wsContext;

	/**
	 * 输出InetSocketAddress、HostAddress和HostName
	 */
	private void get() {
		try {
			MessageContext mc = wsContext.getMessageContext();
			HttpExchange exchange = (HttpExchange) mc.get(JAXWSProperties.HTTP_EXCHANGE);
			InetSocketAddress isa = exchange.getRemoteAddress();
			logger.info("InetSocketAddress : " + isa);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
	}

	/**
	 * 用户名密码校验
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	private boolean checkUserAndPassword(String user, String password) {

		// 获取Properties数据
		Properties properties = new Properties();
		try {
			properties.load(Jws.class.getResourceAsStream("Jws.properties"));

			// user和password都不能为空
			if (null == user || null == password || "".equals(user) || "".equals(password)) {
				logger.info("校验失败！");
				return false;
			}

			// 验证成功返回true
			if (password.equals(properties.getProperty(user))) {
				logger.info("校验成功！");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return false;
	}

	/**
	 * 密码校验并手动提取数据
	 * 
	 * @param password
	 * @param index
	 * @return
	 */
	public boolean manualExtractData(String password, int index) {

		this.get();

		String user = "zhouqingbiao";
		logger.info("user:" + user);
		logger.info("password:" + password);

		// 定义Oracle2Redis
		Oracle2Redis o2R = new Oracle2Redis();

		// 校验密码
		if (checkUserAndPassword(user, password) == true) {
			o2R.addKey(index);
			return true;
		} else {
			logger.info("密码错误！");
			return false;
		}
	}

	/**
	 * 
	 * @param keys
	 * @param index
	 * @param rows
	 * @return
	 */
	public String getData(String keys, int index, int rows) {
		this.get();
		return JSON.toJSONString(new Redis2WebService().getData(keys, index, rows));
	}
}