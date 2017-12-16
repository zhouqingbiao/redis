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

	// 定义静态Logger对象
	static Logger logger = Logger.getGlobal();

	// 定义静态FileHandler对象
	static FileHandler fileHandler = null;

	/**
	 * 获取Logger
	 * 
	 * @return Logger
	 */
	public synchronized static Logger getLogger() {

		// 时间格式化
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// 获取时间
		String date = simpleDateFormat.format(new Date());

		// 文件夹路径
		String pathname = System.getProperty("user.home") + System.getProperty("file.separator") + "log";

		// 日志输出路径
		String pattern = pathname + System.getProperty("file.separator") + date + ".log";

		// 是否追加
		boolean append = true;

		try {
			fileHandler = new FileHandler(pattern, append);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

			// 出错时建立文件夹并回调方法
			File file = new File(pathname);
			file.mkdirs();
			return getLogger();
		}

		// 设置日志级别
		fileHandler.setLevel(Level.ALL);

		// 设置日志格式
		fileHandler.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {

				// 时间格式化
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

				// 获取时间
				String date = "[" + simpleDateFormat.format(new Date()) + "]";

				// 获取级别信息
				String level = "[" + record.getLevel().toString() + "]";

				// 获取类名字
				String sourceClassName = "[" + record.getSourceClassName() + "]";

				// 获取类名字
				String sourceMethodName = "[" + record.getSourceMethodName() + "]";

				// 获取消息
				String message = "[" + record.getMessage() + "]";

				// 返回信息
				return date + " " + level + " " + sourceClassName + " " + sourceMethodName + " " + message
						+ System.getProperty("line.separator");
			}
		});

		// 添加FileHandler到Logger
		logger.addHandler(fileHandler);

		// 返回Logger
		return logger;
	}

	/**
	 * 获取错误信息
	 * 
	 * @param e
	 * @return
	 */
	public synchronized static String getStackTrace(Exception e) {
		StringBuilder stringBuilder = new StringBuilder();

		// 获取错误头
		stringBuilder.append(e.toString()).append(System.getProperty("line.separator"));

		// 获取错误内容
		for (StackTraceElement traceElement : e.getStackTrace())
			stringBuilder.append("\tat " + traceElement).append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
}
