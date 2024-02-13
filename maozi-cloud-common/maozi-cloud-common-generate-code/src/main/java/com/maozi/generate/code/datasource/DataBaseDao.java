/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.maozi.generate.code.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseDao extends TheReflectionClasses {

	/**
	 * ¸üÐÂÊý¾Ý¿â
	 * 
	 * @param sql
	 *            sqlÓï¾ä
	 * @param arr
	 *            ´æ´¢ÐÞ¸ÄµÄÖµ
	 * @return
	 */
	public int databaseUpdate(String sql, ArrayList<Object> arr) {
		int number = 0;
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = C3P0.getDBManager().createDBManager();
			preparedStatement = connection.prepareStatement(sql);
			if (arr != null) {
				for (int i = 0, j = arr.size(); i < j; i++) {
					preparedStatement.setObject(i + 1, arr.get(i));
				}
			}
			number = preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			C3P0.releaseConnection(connection);
		}
		return number;
	}

	/**
	 * Êý¾Ý¿â²éÑ¯
	 * 
	 * @param sql
	 *            sql Óï¾ä
	 * @param arrayList
	 *            ²éÑ¯Ìõ¼þÄÚÈÝ
	 * @param assignClass
	 *            ·µ»Ø¼¯ºÏÀàÀàÐÍ
	 * @return
	 */
	public List databaseSelect(String sql, ArrayList<Object> arrayList, Class assignClass) {
		List list = null;
		List<Map<String, Object>> rultlist = new ArrayList<Map<String, Object>>();
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = C3P0.getDBManager().createDBManager();
			preparedStatement = connection.prepareStatement(sql);
			if (arrayList != null) {
				for (int i = 0, j = arrayList.size(); i < j; i++) {
					preparedStatement.setObject(i + 1, arrayList.get(i));
				}
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				if (resultSet != null) {
					ResultSetMetaData data = resultSet.getMetaData();
					while (resultSet.next()) {
						Map<String, Object> map = new HashMap<String, Object>();
						for (int i = 1, j = data.getColumnCount(); i <= j; i++) {
							System.out.println(data.getColumnName(i) + "\t" + resultSet.getObject(i));
							map.put(data.getColumnName(i), resultSet.getObject(i));
						}
						rultlist.add(map);
					}
				}
				list = TheReflectionClasses.ToObjectList(null, assignClass, rultlist);
				return list;
			} catch (Exception e) {
				System.out.println("ÄÚ²¿±¨´í");
				System.out.println(e.getLocalizedMessage());
			}
		} catch (Exception e) {
			System.out.println("Íâ²¿±¨´í");
			System.out.println(e.getLocalizedMessage());
		} finally {
			C3P0.releaseConnection(connection);
		}
		return null;
	}

	public int databaseSelect(String sql, ArrayList<Object> arrayList) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = C3P0.getDBManager().createDBManager();
			preparedStatement = connection.prepareStatement(sql);
			if (arrayList != null) {
				for (int i = 0, j = arrayList.size(); i < j; i++) {
					preparedStatement.setObject(i + 1, arrayList.get(i));
				}
			}
			int number = 0;
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				if (resultSet.next()) {
					number = resultSet.getInt(1);
				}
				return number;
			} catch (Exception e) {

			}
		} catch (Exception e) {

		} finally {
			C3P0.releaseConnection(connection);
		}
		return 0;
	}

}
