package com.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Reggol {

	// ���徲̬Logger����
	static Logger logger = Logger.getGlobal();

	// ���徲̬FileHandler����
	static FileHandler fileHandler = null;

	/**
	 * ��ȡLogger
	 * 
	 * @return Logger
	 */
	public synchronized static Logger getLogger() {

		// ʱ���ʽ��
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// ��ȡʱ��
		String date = simpleDateFormat.format(new Date());

		// �ļ���·��
		String pathname = System.getProperty("user.home") + System.getProperty("file.separator") + "log";

		// ��־���·��
		String pattern = pathname + System.getProperty("file.separator") + date + ".log";

		// �Ƿ�׷��
		boolean append = true;

		try {
			fileHandler = new FileHandler(pattern, append);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

			// ����ʱ�����ļ��в��ص�����
			File file = new File(pathname);
			file.mkdirs();
			return getLogger();
		}

		// ������־����
		fileHandler.setLevel(Level.ALL);

		// ������־��ʽ
		fileHandler.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {

				// ʱ���ʽ��
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

				// ��ȡʱ��
				String date = "[" + simpleDateFormat.format(new Date()) + "]";

				// ��ȡ������Ϣ
				String level = "[" + record.getLevel().toString() + "]";

				// ��ȡ������
				String sourceClassName = "[" + record.getSourceClassName() + "]";

				// ��ȡ������
				String sourceMethodName = "[" + record.getSourceMethodName() + "]";

				// ��ȡ��Ϣ
				String message = "[" + record.getMessage() + "]";

				// ������Ϣ
				return date + " " + level + " " + sourceClassName + " " + sourceMethodName + " " + message
						+ System.getProperty("line.separator");
			}
		});

		// ���FileHandler��Logger
		logger.addHandler(fileHandler);

		// ����Logger
		return logger;
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @param e
	 * @return
	 */
	public synchronized static String getStackTrace(Exception e) {
		StringBuilder stringBuilder = new StringBuilder();

		// ��ȡ����ͷ
		stringBuilder.append(e.toString()).append(System.getProperty("line.separator"));

		// ��ȡ��������
		for (StackTraceElement traceElement : e.getStackTrace())
			stringBuilder.append("\tat " + traceElement).append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
}
