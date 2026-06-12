package com.maozi.base.plugin;

import com.maozi.base.enums.StoreClassType;
import com.maozi.common.CollectionUtil;

import java.util.Map;

/**
 * 存储类注册中心
 * <p>
 * 按存储类型（如数据库）维护实体类名称与 Class 对象的映射关系，
 * 供框架运行时根据类名查找对应的实体 Class。
 * </p>
 * <p>
 * 使用场景：在动态查询构建（如 MyBatis-Plus JOIN）过程中，
 * 框架需要根据字符串形式的类名找到对应的 Class 对象，
 * 以实现反射操作、字段映射等功能。
 * </p>
 *
 * @author maozi
 */
public class StoreClass {

    /** 存储类型 -> (类名 -> Class对象) 的映射 */
    public static Map<StoreClassType,Map<String,Class<?>>> storeClassMap = CollectionUtil.newHashMap();

}
