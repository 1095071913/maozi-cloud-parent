package com.maozi.base.enums;

import com.maozi.base.IEnum;
import lombok.Getter;
import lombok.Setter;

public enum StoreClassType implements IEnum {

    db(0,"数据库");

    StoreClassType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

    }

    @Getter
    @Setter
    private Integer value;

    @Getter
    @Setter
    private String desc;

}
