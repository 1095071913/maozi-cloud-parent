package com.maozi.common;

import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CollectionUtil {

    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T ... array) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

    public static <K,V> Map<K,V> newHashMap() {
        return new HashMap<>();
    }

    public static <T> Set<T> newHashSet() {
        return new HashSet<>();
    }

    public static <K,V> Map<K,V> newHashMap(Map<K,V> oldMap) {
        return new HashMap<>(oldMap);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?,?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?,?> map) {
        return !isEmpty(map);
    }

    public static <T> Collection<T> collectionIsEmptyThrowError(Collection<T> collection,String resourceName) {

        if(ObjectUtil.isNullEmpty(collection)) {
            throw new BusinessResultException(SystemErrorCode.DATA_NOT_EXIST_ERROR)
                    .setResource(resourceName);
        }

        return collection;

    }

    public static <K,V> Map<K,V> collectionIsEmptyThrowError(Map<K,V> map,String resourceName) {

        if(ObjectUtil.isNullEmpty(map)) {
            throw new BusinessResultException(SystemErrorCode.DATA_NOT_EXIST_ERROR)
                    .setResource(resourceName);
        }

        return map;

    }

}
