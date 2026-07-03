package com.maozi.opentelemetry.sampler.enums;

import com.maozi.opentelemetry.sampler.handle.filter.FilterHandle;
import com.maozi.opentelemetry.sampler.handle.filter.impl.HttpFilterHandle;
import com.maozi.opentelemetry.sampler.handle.filter.impl.MysqlFilterHandle;
import com.maozi.opentelemetry.sampler.handle.filter.impl.RedisFilterHandle;
import lombok.Getter;

public enum FilterType {

    HTTP(0,"Http",new HttpFilterHandle()),

    REDIS(1,"Redis", new RedisFilterHandle()),

    MYSQL(2,"Mysql",new MysqlFilterHandle()),

    ;


    FilterType(Integer value, String desc, FilterHandle handle){

        this.value = value;

        this.desc = desc;

        this.handle = handle;

    };

    @Getter
    private final Integer value;

    @Getter
    private final String desc;

    @Getter
    private final FilterHandle handle;

}
