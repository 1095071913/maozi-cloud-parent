package com.maozi.common;

import cn.hutool.core.util.StrUtil;

public class ReflectUtil extends cn.hutool.core.util.ReflectUtil {

    public static final String GET_FUN_PREFIX_NAME = "get";

    public static final String SET_FUN_PREFIX_NAME = "set";

    public static final String GET_ID_FUN_NAME = "getId";

    public static <R> R invokeGet(Object obj, String fieldName) {
        return cn.hutool.core.util.ReflectUtil.invoke(obj, getter(fieldName));
    }

    public static void invokeSet(Object obj, String fieldName, Object value) {
        cn.hutool.core.util.ReflectUtil.invoke(obj, setter(fieldName), value);
    }

    public static <R> R invokeFunGetId(Object obj){
        return cn.hutool.core.util.ReflectUtil.invoke(obj, GET_ID_FUN_NAME);
    }

    public static String getter(String name) {
        return GET_FUN_PREFIX_NAME + StrUtil.upperFirst(name);
    }

    public static String setter(String name) {
        return SET_FUN_PREFIX_NAME + StrUtil.upperFirst(name);
    }

}