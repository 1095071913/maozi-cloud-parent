package com.maozi.base.plugin;

import com.maozi.base.enums.StoreClassType;
import com.maozi.common.CollectionUtil;

import java.util.Map;

public class StoreClass {

    public static Map<StoreClassType,Map<String,Class<?>>> storeClassMap = CollectionUtil.newHashMap();

}
