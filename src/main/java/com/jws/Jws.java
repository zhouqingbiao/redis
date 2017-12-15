package com.jws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jws.WebService;

import org.json.JSONArray;

import com.fwzl.Redis4Fwzl;
import com.log.Reggol;

@WebService
public class Jws {
	// ���Logger
	static Logger logger = Reggol.getLogger();

	private boolean checkUserAndPassword(String user, String password) {

		// ��ȡProperties����
		Properties properties = new Properties();
		try {
			properties.load(Jws.class.getResourceAsStream("Jws.properties"));

			// user��password������Ϊ��
			if (null == user || null == password || "".equals(user) || "".equals(password)) {
				logger.info("У��ʧ�ܣ�");
				return false;
			}

			// ��֤�ɹ�����true
			if (password.equals(properties.getProperty(user))) {
				logger.info("У��ɹ���");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getFwzl(String user, String password, String fwzl) {

		JSONArray jSONArray = new JSONArray();

		logger.info("user:" + user);
		logger.info("password:" + password);

		// У������
		if (checkUserAndPassword(user, password) == false) {
			return jSONArray.toString();
		}

		ArrayList<Map<String, String>> arrayList = new Redis4Fwzl().getFwzl4Redis(fwzl);

		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				jSONArray.put(arrayList.get(i));
			}
		}

		return jSONArray.toString();
	}
}
