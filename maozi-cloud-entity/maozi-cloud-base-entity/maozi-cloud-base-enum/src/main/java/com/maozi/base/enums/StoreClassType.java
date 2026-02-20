package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

public enum StoreClassType implements BaseEnum {

    DB(0,"数据库");

    StoreClassType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

    }

    @Getter
    private final Integer value;

    @Getter
    private final String desc;

    @Override
    public String toString() {
        return value+"."+desc;
    }

}
