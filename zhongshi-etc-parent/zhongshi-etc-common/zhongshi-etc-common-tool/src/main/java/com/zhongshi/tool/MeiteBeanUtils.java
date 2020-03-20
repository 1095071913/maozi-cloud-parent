package com.zhongshi.tool;

import org.springframework.beans.BeanUtils;

/**
 * 功能说明:	VO与DO转换
 * 功能作者:	彭晋龙
 * 创建日期:	2019.8.14
 * 版权归属:	忧伤-蓝河
 */

public class MeiteBeanUtils<Vo, Do> {

	/**
	 * Vo 转换为Do 工具类
	 * 
	 * @param dtoEntity
	 * @param doEntity
	 * @return
	 */
	public static <Do> Do voToDo(Object dtoEntity, Class<Do> doClass) {
		// 判断dto是否为空!
		if (dtoEntity == null) {
			return null;
		}
		// 判断DoClass 是否为空
		if (doClass == null) {
			return null;
		}
		try {
			Do newInstance = doClass.newInstance();
			BeanUtils.copyProperties(dtoEntity, newInstance);
			// Dto转换Do
			return newInstance;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * do 转换为Dto 工具类
	 * 
	 * @param dtoEntity
	 * @param doEntity
	 * @return
	 */
	public static <Dto> Dto doToDto(Object doEntity, Class<Dto> dtoClass) {
		// 判断dto是否为空!
		if (doEntity == null) {
			return null;
		}
		// 判断DoClass 是否为空
		if (dtoClass == null) {
			return null;
		}
		try {
			Dto newInstance = dtoClass.newInstance();
			BeanUtils.copyProperties(doEntity, newInstance);
			// Dto转换Do
			return newInstance;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * do 转换为Dto 工具类 支持集合类型
	 * 
	 * @param dtoEntity
	 * @param doEntity
	 * @return
	 */
	public static <Dto> Dto listDoToDto(Object doEntity, Class<Dto> dtoClass) {
		// 判断dto是否为空!
		if (doEntity == null) {
			return null;
		}
		// 判断DoClass 是否为空
		if (dtoClass == null) {
			return null;
		}
		try {
			Dto newInstance = dtoClass.newInstance();
			BeanUtils.copyProperties(doEntity, newInstance);
			// Dto转换Do
			return newInstance;
		} catch (Exception e) {
			return null;
		}
	}

}
