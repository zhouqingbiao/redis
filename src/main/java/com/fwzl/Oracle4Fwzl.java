package com.fwzl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.oracle.Oracle;

public class Oracle4Fwzl implements Job {
	public static void main(String[] args) {
		new Oracle4Fwzl().doOracle4Fwzl();
	}

	public void doOracle4Fwzl() {

		// OracleLike开始时间
		long startTime4OracleOfLike = System.nanoTime();

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		// 获取fwzl信息
		StringBuffer sql = new StringBuffer(
				"select t.fwzl from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201");

		// Like信息
		String fwzl_like = "濮家 13";

		// 去除头尾空格
		fwzl_like = fwzl_like.trim();

		// 如果有空格则执行分割组装
		if (fwzl_like.indexOf(" ") != -1) {
			String[] string_fwzl = fwzl_like.split(" ");
			for (int i = 0; i < string_fwzl.length; i++) {
				sql.append(" and t.fwzl like ?");
			}
		}

		PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql.toString());

		// SQL赋值
		if (fwzl_like.indexOf(" ") != -1) {
			String[] string_fwzl = fwzl_like.split(" ");
			for (int i = 0; i < string_fwzl.length; i++) {
				try {
					preparedStatement.setString(i + 1, "%" + string_fwzl[i] + "%");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		ResultSet resultSet = jracle.getResultSet(preparedStatement);

		// 数据存入Map
		List<String> list = new ArrayList<String>();
		try {
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// OracleLike停止时间
		long stopTime4OracleOfLike = System.nanoTime();
		System.out.println("开始时间（OracleLike）：" + startTime4OracleOfLike);
		System.out.println("停止时间（OracleLike）：" + stopTime4OracleOfLike);

		// Oracle总时间消耗
		System.out.println("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) + "纳秒");
		System.out.println("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000 + "毫秒");
		System.out.println("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000000 + "秒");
		jracle.close(resultSet, preparedStatement, connection);

		// ------------------------------------------------------------------------------------------------

		// Oracle开始时间
		long startTime4Oracle = System.nanoTime();
		String fwzl = "濮家东村13幢3单元303室";
		if (!"".equals(fwzl) || null == fwzl) {
			Map<String, String> map = new Oracle4Fwzl().getFwzl(fwzl);
			Set<String> set_map = map.keySet();
			for (String string : set_map) {
				System.out.println(string + "：" + map.get(string));
			}
		}

		// Oracle停止时间
		long stopTime4Oracle = System.nanoTime();
		System.out.println("开始时间（Oracle）：" + startTime4Oracle);
		System.out.println("停止时间（Oracle）：" + stopTime4Oracle);

		// Oracle总时间消耗
		System.out.println("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) + "纳秒");
		System.out.println("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) / 1000000 + "毫秒");
		System.out.println("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) / 1000000000 + "秒");

		// ----------------------------------------------------------------------------------------------------------

		System.out.println("查询关键字：" + fwzl_like);
		System.out.println("OracleLike消耗时间：" + (stopTime4OracleOfLike - startTime4OracleOfLike) + "纳秒");
		System.out.println("OracleLike消耗时间：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000 + "毫秒");
		System.out.println("OracleLike消耗时间：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000000 + "秒");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------");
		System.out.println("精确查询：" + fwzl);
		System.out.println("Oracle消耗时间：" + (stopTime4Oracle - startTime4Oracle) + "纳秒");
		System.out.println("Oracle消耗时间：" + (stopTime4Oracle - startTime4Oracle) / 1000000 + "毫秒");
		System.out.println("Oracle消耗时间：" + (stopTime4Oracle - startTime4Oracle) / 1000000000 + "秒");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------");
		System.out.println("总消耗时间："
				+ ((stopTime4OracleOfLike - startTime4OracleOfLike) + (stopTime4Oracle - startTime4Oracle)) + "纳秒");
		System.out.println("总消耗时间：" + ((stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000
				+ (stopTime4Oracle - startTime4Oracle) / 1000000) + "毫秒");
		System.out.println("总消耗时间：" + ((stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000000
				+ (stopTime4Oracle - startTime4Oracle) / 1000000000) + "秒");

	}

	public Map<String, String> getFwzl(String fwzl) {

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		// 获取fwzl信息
		String sql = "select t.* from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201 and t.fwzl = ?";

		PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);

		try {
			preparedStatement.setString(1, fwzl);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ResultSet resultSet = jracle.getResultSet(preparedStatement);

		// 数据存入Map
		Map<String, String> map = new HashMap<String, String>();
		try {
			while (resultSet.next()) {
				map.put(resultSet.getMetaData().getColumnName(1), resultSet.getString(1));
				map.put(resultSet.getMetaData().getColumnName(2), resultSet.getString(2));
				map.put(resultSet.getMetaData().getColumnName(3), resultSet.getString(3));
				map.put(resultSet.getMetaData().getColumnName(4), resultSet.getString(4));
				map.put(resultSet.getMetaData().getColumnName(5), resultSet.getString(5));
				map.put(resultSet.getMetaData().getColumnName(6), resultSet.getString(6));
				map.put(resultSet.getMetaData().getColumnName(7), resultSet.getString(7));
				map.put(resultSet.getMetaData().getColumnName(8), resultSet.getString(8));
				map.put(resultSet.getMetaData().getColumnName(9), resultSet.getString(9));
				map.put(resultSet.getMetaData().getColumnName(10), resultSet.getString(10));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jracle.close(resultSet, preparedStatement, connection);
		return map;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		doOracle4Fwzl();
	}

}
