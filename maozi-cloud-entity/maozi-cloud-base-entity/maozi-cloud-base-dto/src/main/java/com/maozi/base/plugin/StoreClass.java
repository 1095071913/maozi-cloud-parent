package com.maozi.base.plugin;

import com.google.common.collect.Maps;
import com.maozi.base.enums.StoreClassType;
import java.util.Map;

public class StoreClass {

    public static Map<StoreClassType,Map<String,Class<?>>> storeClassMap = Maps.newHashMap();

}
