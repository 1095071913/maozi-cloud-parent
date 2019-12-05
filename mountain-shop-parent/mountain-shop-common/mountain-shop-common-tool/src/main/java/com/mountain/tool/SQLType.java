package com.mountain.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

/**
 * ������
 * 
 * @author Administrator
 *
 */
public class SQLType {

	/**
	 * ��ͷ��ĸ��Сд����
	 * 
	 * @param letter
	 *            ��ĸ�ַ���
	 * @return ���ؿ�ͷ��д��ĸ
	 */
	public static String initial(String letter) {
		
		return letter.substring(0, 1).toUpperCase() + letter.substring(1);
	}
	
	public static String initial1(String letter) {
		
		return letter.substring(0, 1).toLowerCase() + letter.substring(1);
	}

	/**
	 * Java�������ݿⷽ��
	 * 
	 * @param ���ݿ����������ַ�������
	 * @return java���������ַ���
	 */
	public static String types(String type) {
		type = type.toUpperCase();
		if (type.startsWith("INT(") || type.startsWith("TINYINT(") || type.startsWith("SMALLINT(")
				|| type.startsWith("MEDIUMINT(")) {
			type = "int"; 
		}else if(type.startsWith("BIGINT(")){  
			return "Integer";
		}else if (type.startsWith("FLOAT(") || type.startsWith("DOUBLE") || type.startsWith("DECIMAL(")) {
			type = "java.math.BigDecimal";
		} else if (type.startsWith("DATETIME")||type.startsWith("TIME") || type.startsWith("YEAR")
				|| type.startsWith("TIMESTAMP")) {
			return "java.sql.Timestamp";
		} else if (type.startsWith("DATE")) {
			return "java.sql.Date";
		} else {
			type = "String";
		}
		return type;
	}

	/**
	 * ʱ��ת���ַ�������
	 * 
	 * @param date
	 *            ʱ��Date���Ͳ���
	 * @return ת�����ַ���
	 */
	public static String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * Date����ת���ַ�������
	 * 
	 * @param date
	 * @return ת������ַ���
	 */
	public static String stringToDate(String date) {
		String str = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		// 07-1-18
		format = DateFormat.getDateInstance(DateFormat.SHORT);
		str = format.format(date);

		// 2007-1-18
		format = DateFormat.getDateInstance(DateFormat.MEDIUM);
		str = format.format(date);

		// 2007��1��18�� ������
		format = DateFormat.getDateInstance(DateFormat.FULL);
		str = format.format(date);

		return str;
	}

	/**
	 * ��ѧ�������Сֵ�����ֵ������� �ķ���
	 * 
	 * @param min
	 *            ��Сֵ
	 * @param max
	 *            ���ֵ
	 * @return ��Сֵ�����ֵ�������
	 */
	public static int mathematics(int min, int max) {
		return Math.random() > 0.5 ? (int) (Math.random() * (max - min + 1) + min)
				: ((int) (Math.random() * (max - min + 1) + min));//-((int) (Math.random() * (max - min + 1) + min))
	};
	
	
	public static void fileCreate(String Pash,String tableName,StringBuilder entity) throws Exception {
		fileCreate(Pash,tableName,entity,null);
	}
	
	public static void fileCreate(String Pash,String tableName,StringBuilder entity,String fileType) throws Exception {
		File file = new File(Pash);
		if(!file.exists()){
			file.mkdir();
		}
		FileOutputStream fStream = new FileOutputStream(new File(Pash + "\\" + SQLType.initial(tableName)+ "." + (StringUtils.isEmpty(fileType)?"java":fileType)));
		fStream.write(entity.toString().getBytes());
		fStream.close();
	}
}
