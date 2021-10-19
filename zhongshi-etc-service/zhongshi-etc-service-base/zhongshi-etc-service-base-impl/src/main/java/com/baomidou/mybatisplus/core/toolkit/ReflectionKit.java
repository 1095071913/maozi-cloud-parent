/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * 鍙嶅皠宸ュ叿绫伙紝鎻愪緵鍙嶅皠鐩稿叧鐨勫揩鎹锋搷浣�
 *
 * @author Caratacus
 * @author hcl
 * @since 2016-09-22
 */
public final class ReflectionKit {
    private static final Log logger = LogFactory.getLog(ReflectionKit.class);
    /**
     * class field cache
     */
    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(8);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
    }

    /**
     * <p>
     * 鍙嶅皠 method 鏂规硶鍚嶏紝渚嬪 setVersion
     * </p>
     *
     * @param field Field
     * @param str   String JavaBean绫荤殑version灞炴�у悕
     * @return version灞炴�х殑setter鏂规硶鍚嶇О锛宔.g. setVersion
     * @deprecated 3.0.8
     */
    @Deprecated
    public static String setMethodCapitalize(Field field, final String str) {
        return StringUtils.concatCapitalize("set", str);
    }

    /**
     * 鑾峰彇瀛楁鍊�
     *
     * @param entity    瀹炰綋
     * @param fieldName 瀛楁鍚嶇О
     * @return 灞炴�у��
     */
    public static Object getFieldValue(Object entity, String fieldName) {
        Class<?> cls = entity.getClass();
        Map<String, Field> fieldMaps = getFieldMap(cls);
        try {
            Field field = fieldMaps.get(fieldName);
            Assert.notNull(field, "Error: NoSuchField in %s for %s.  Cause:", cls.getSimpleName(), fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe("Error: Cannot read field in %s.  Cause:", e, cls.getSimpleName());
        }
    }

    /**
     * <p>
     * 鍙嶅皠瀵硅薄鑾峰彇娉涘瀷
     * </p>
     *
     * @param clazz 瀵硅薄
     * @param index 娉涘瀷鎵�鍦ㄤ綅缃�
     * @return Class
     */
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
    	
    	Boolean boo=false;
    	
    	Type[] params = null;
    	
    	for(Class<?> clazzBack = clazz;;clazzBack=clazzBack.getSuperclass()) {
    		
    		Type genType = clazzBack.getGenericSuperclass(); 
    		
//            if (!(genType instanceof ParameterizedType)) {
//            	BaseResultFactory.log.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
//            }
            
    		try {params = ((ParameterizedType) genType).getActualTypeArguments();}catch (Exception e) {
				continue;
			}
            
//            if (index >= params.length || index < 0) {
//                BaseResultFactory.log.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index,clazz.getSimpleName(), params.length));
//            }
            
            if (params[index] instanceof Class) {
//            	BaseResultFactory.log.warn(String.format("Warn: %s not set the actual class on superclass generic parameter",clazz.getSimpleName()));
            	boo=true;
            	break;
            }
            
    	}
        
        return boo?(Class<?>) params[index]:Object.class;

    }

    /**
     * <p>
     * 鑾峰彇璇ョ被鐨勬墍鏈夊睘鎬у垪琛�
     * </p>
     *
     * @param clazz 鍙嶅皠绫�
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        List<Field> fieldList = getFieldList(clazz);
        return CollectionUtils.isNotEmpty(fieldList) ? fieldList.stream().collect(Collectors.toMap(Field::getName, field -> field)) : Collections.emptyMap();
    }

    /**
     * <p>
     * 鑾峰彇璇ョ被鐨勬墍鏈夊睘鎬у垪琛�
     * </p>
     *
     * @param clazz 鍙嶅皠绫�
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        return CollectionUtils.computeIfAbsent(CLASS_FIELD_CACHE, clazz, k -> {
            Field[] fields = k.getDeclaredFields();
            List<Field> superFields = new ArrayList<>();
            Class<?> currentClass = k.getSuperclass();
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                Collections.addAll(superFields, declaredFields);
                currentClass = currentClass.getSuperclass();
            }
            /* 鎺掗櫎閲嶈浇灞炴�� */
            Map<String, Field> fieldMap = excludeOverrideSuperField(fields, superFields);
            /*
             * 閲嶅啓鐖剁被灞炴�ц繃婊ゅ悗澶勭悊蹇界暐閮ㄥ垎锛屾敮鎸佽繃婊ょ埗绫诲睘鎬у姛鑳�
             * 鍦烘櫙锛氫腑闂磋〃涓嶉渶瑕佽褰曞垱寤烘椂闂达紝蹇界暐鐖剁被 createTime 鍏叡灞炴��
             * 涓棿琛ㄥ疄浣撻噸鍐欑埗绫诲睘鎬� ` private transient Date createTime; `
             */
            return fieldMap.values().stream()
                    /* 杩囨护闈欐�佸睘鎬� */
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    /* 杩囨护 transient鍏抽敭瀛椾慨楗扮殑灞炴�� */
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * <p>
     * 鑾峰彇璇ョ被鐨勬墍鏈夊睘鎬у垪琛�
     * </p>
     *
     * @param clazz 鍙嶅皠绫�
     * @deprecated 3.4.0
     */
    @Deprecated
    public static List<Field> doGetFieldList(Class<?> clazz) {
        if (clazz.getSuperclass() != null) {
            /* 鎺掗櫎閲嶈浇灞炴�� */
            Map<String, Field> fieldMap = excludeOverrideSuperField(clazz.getDeclaredFields(),
                    /* 澶勭悊鐖剁被瀛楁 */
                    getFieldList(clazz.getSuperclass()));
            /*
             * 閲嶅啓鐖剁被灞炴�ц繃婊ゅ悗澶勭悊蹇界暐閮ㄥ垎锛屾敮鎸佽繃婊ょ埗绫诲睘鎬у姛鑳�
             * 鍦烘櫙锛氫腑闂磋〃涓嶉渶瑕佽褰曞垱寤烘椂闂达紝蹇界暐鐖剁被 createTime 鍏叡灞炴��
             * 涓棿琛ㄥ疄浣撻噸鍐欑埗绫诲睘鎬� ` private transient Date createTime; `
             */
            return fieldMap.values().stream()
                    /* 杩囨护闈欐�佸睘鎬� */
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    /* 杩囨护 transient鍏抽敭瀛椾慨楗扮殑灞炴�� */
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * <p>
     * 鎺掑簭閲嶇疆鐖剁被灞炴��
     * </p>
     *
     * @param fields         瀛愮被灞炴��
     * @param superFieldList 鐖剁被灞炴��
     */
    public static Map<String, Field> excludeOverrideSuperField(Field[] fields, List<Field> superFieldList) {
        // 瀛愮被灞炴��
        Map<String, Field> fieldMap = Stream.of(fields).collect(toMap(Field::getName, identity(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
        superFieldList.stream().filter(field -> !fieldMap.containsKey(field.getName()))
                .forEach(f -> fieldMap.put(f.getName(), f));
        return fieldMap;
    }

    /**
     * 鍒ゆ柇鏄惁涓哄熀鏈被鍨嬫垨鍩烘湰鍖呰绫诲瀷
     *
     * @param clazz class
     * @return 鏄惁鍩烘湰绫诲瀷鎴栧熀鏈寘瑁呯被鍨�
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz));
    }

}
