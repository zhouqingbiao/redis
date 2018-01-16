package com.jws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.fwzl.Redis4Fwzl;
import com.fwzl.Redis4FwzlWithColumnName;
import com.fwzl.Redis4HzFwdjTpfJcdjb;
import com.fwzl.SelectHzFwdjTpfJcdjb;

@WebService
public class Jws {
	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

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

	public void manualExtractData(String password) {

		String user = "zhouqingbiao";
		logger.info("user:" + user);
		logger.info("password:" + password);

		// 校验密码
		if (checkUserAndPassword(user, password) == true) {
			// new SelectHzGisTpsFw().addKey();
			new SelectHzFwdjTpfJcdjb().addKey();
		}
	}

	public String selectHzFwdjTpfJcdjb(String fwzl) {

		JSONArray jSONArray = new JSONArray();

		// WithColumnName
		ArrayList<Map<String, String>> arrayList = new Redis4HzFwdjTpfJcdjb().getData(fwzl);

		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				jSONArray.put(arrayList.get(i));
			}
		}

		return jSONArray.toString();
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param fwzl
	 * @return
	 */
	public String getFwzlWithCheck(String user, String password, String fwzl) {

		JSONArray jSONArray = new JSONArray();

		logger.info("user:" + user);
		logger.info("password:" + password);

		// 校验密码
		if (checkUserAndPassword(user, password) == false) {
			return jSONArray.toString();
		}

		// WithColumnName
		ArrayList<Map<String, String>> arrayList = new Redis4FwzlWithColumnName().getFwzl4Redis(fwzl);

		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				jSONArray.put(arrayList.get(i));
			}
		}

		return jSONArray.toString();
	}

	/**
	 * 
	 * @param fwzl
	 * @return
	 */
	public String getFwzl(String fwzl) {
		// NoColumnName
		return new Redis4Fwzl().getFwzl4Redis(fwzl);
	}

	/**
	 * 
	 * @param fwzl
	 * @return
	 */
	public String getFwzlWithColumnName(String fwzl) {

		JSONArray jSONArray = new JSONArray();

		// WithColumnName
		ArrayList<Map<String, String>> arrayList = new Redis4FwzlWithColumnName().getFwzl4Redis(fwzl);

		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				jSONArray.put(arrayList.get(i));
			}
		}

		return jSONArray.toString();
	}
}
