package com.jws;

import java.io.IOException;
import java.util.Properties;

import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.data.Redis4HzFwdjTpfJcdjb;
import com.data.Redis4HzGisTpsFw;
import com.data.Redis4HzGisTpsFwWithColumnName;
import com.data.Redis4HzGisTpsZrz;
import com.data.SelectHzFwdjTpfJcdjb;
import com.data.SelectHzGisTpsFw;
import com.data.SelectHzGisTpsZrz;

@WebService
public class Jws {
	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

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
	 */
	public void manualExtractData(String password, int index) {

		String user = "zhouqingbiao";
		logger.info("user:" + user);
		logger.info("password:" + password);

		// 校验密码
		if (checkUserAndPassword(user, password) == true) {
			switch (index) {
			case 0:
				new SelectHzGisTpsFw().addKey();
				break;

			case 1:
				new SelectHzFwdjTpfJcdjb().addKey();
				break;

			case 2:
				new SelectHzGisTpsZrz().addKey();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * HzGisTpsZrz
	 * 
	 * @param keys
	 * @return
	 */
	public String SelectHzGisTpsZrz(String keys) {
		return JSON.toJSONString(new Redis4HzGisTpsZrz().getData(keys));
	}

	/**
	 * HzFwdjTpfJcdjb
	 * 
	 * @param keys
	 * @return
	 */
	public String selectHzFwdjTpfJcdjb(String keys) {

		return JSON.toJSONString(new Redis4HzFwdjTpfJcdjb().getData(keys));
	}

	/**
	 * HzGisTpsFw--返回所有列
	 * 
	 * @param keys
	 * @return
	 */
	public String selectHzGisTpsFw(String keys) {
		return JSON.toJSONString(new Redis4HzGisTpsFw().getData(keys));
	}

	/**
	 * HzGisTpsFw--返回所有列
	 * 
	 * @param keys
	 * @return
	 */
	public String selectHzGisTpsFwWithColumnName(String keys) {

		return JSON.toJSONString(new Redis4HzGisTpsFwWithColumnName().getData(keys));
	}

	/**
	 * HzGisTpsFw--校验用户名密码并返回所有列
	 * 
	 * @param user
	 * @param password
	 * @param keys
	 * @return
	 */
	public String selectHzGisTpsFwWithColumnNameAndCheck(String user, String password, String keys) {

		logger.info("user:" + user);
		logger.info("password:" + password);

		// 校验密码
		if (checkUserAndPassword(user, password) == false) {
			return null;
		}

		return JSON.toJSONString(new Redis4HzGisTpsFwWithColumnName().getData(keys));
	}
}
