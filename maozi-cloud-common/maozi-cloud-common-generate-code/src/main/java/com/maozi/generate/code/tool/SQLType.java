package com.maozi.generate.code.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.springframework.util.ObjectUtils;

/**
 *       
 * 
 * @author Administrator
 *
 */
public class SQLType {

	/**
	 *   ͷ  ĸ  Сд    
	 * 
	 * @param letter
	 *              ĸ ַ   
	 * @return    ؿ ͷ  д  ĸ
	 */
	public static String initial(String letter) {
		
		return letter.substring(0, 1).toUpperCase() + letter.substring(1);
	}
	
	public static String initial1(String letter) {
		
		return letter.substring(0, 1).toLowerCase() + letter.substring(1);
	}
	
	public static String underlineToCapital(String str) {
		
		char [] charArray = str.toCharArray();
		
		char [] newCharArray = new char[charArray.length - str.split("_").length + 1];
		
		int index = 0;
		
		Boolean capital = false;
		
		for(char strChar : charArray) {
			
			if(strChar != 95) {
				
				newCharArray[index] = strChar;
				
				if(capital) {
					
					newCharArray[index]-=32;
					
					capital = false;
					
				}
				
				++index;
				
			}else {
				
				capital = true;
			
			}
			
		}
		
		return new String(newCharArray);
		
	}

	/**
	 * Java       ݿⷽ  
	 * 
	 * @param    ݿ          ַ       
	 * @return java         ַ   
	 */
	public static String types(String type) {
		
		type = type.toUpperCase();
		
		if (type.startsWith("INT") || type.startsWith("TINYINT") || type.startsWith("SMALLINT") || type.startsWith("MEDIUMINT")) {
			
			type = "int"; 
			
		}else if(type.startsWith("BIGINT")){  
			
			return "Long";
			
		}else if (type.startsWith("FLOAT") || type.startsWith("DOUBLE") || type.startsWith("DECIMAL")) {
			
			type = "java.math.BigDecimal";
			
		} else if (type.startsWith("DATETIME") || type.startsWith("TIME") || type.startsWith("YEAR") || type.startsWith("TIMESTAMP")) {
			
			return "java.sql.Timestamp";
			
		} else if (type.startsWith("DATE")) {
			
			return "java.sql.Date";
			
		} else {
			
			type = "String";
			
		}
		return type;
	}

	/**
	 * ʱ  ת   ַ       
	 * 
	 * @param date
	 *            ʱ  Date   Ͳ   
	 * @return ת     ַ   
	 */
	public static String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * Date    ת   ַ       
	 * 
	 * @param date
	 * @return ת      ַ   
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

		// 2007  1  18         
		format = DateFormat.getDateInstance(DateFormat.FULL);
		str = format.format(date);

		return str;
	}

	/**
	 *   ѧ       Сֵ     ֵ         ķ   
	 * 
	 * @param min
	 *              Сֵ
	 * @param max
	 *               ֵ
	 * @return   Сֵ     ֵ       
	 */
	public static int mathematics(int min, int max) {
		return Math.random() > 0.5 ? (int) (Math.random() * (max - min + 1) + min)
				: ((int) (Math.random() * (max - min + 1) + min));//-((int) (Math.random() * (max - min + 1) + min))
	};
	
	
	public static void fileCreate(String Pash,String tableName,StringBuilder entity) throws Exception {
		fileCreate(Pash,tableName,entity,null);
	}
	
	public static void fileCreate(String Pash,String fileName,StringBuilder entity,String fileType) throws Exception {
		
		File file1 = new File(Pash);
		if(!file1.exists()){
			file1.mkdirs();
		}
		
		if(!ObjectUtils.isEmpty(entity)) {

			File file2 = new File(Pash + "\\" + fileName+ "." + (ObjectUtils.isEmpty(fileType)?"java":fileType));
			if(!file1.exists()) {
				file2.createNewFile();
			}
			
			FileOutputStream fStream = new FileOutputStream(file2);
			fStream.write(entity.toString().getBytes());
			fStream.close();
			
		}
		
	}
	
}
