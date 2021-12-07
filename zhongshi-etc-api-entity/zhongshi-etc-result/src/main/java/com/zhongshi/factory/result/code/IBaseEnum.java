package com.zhongshi.factory.result.code;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IEnum;

public interface IBaseEnum<K,V extends Serializable> extends IEnum<V> {

    K getKey();
    
    V getValue();
    
}