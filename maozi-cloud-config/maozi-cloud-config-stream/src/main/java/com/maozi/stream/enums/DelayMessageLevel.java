package com.maozi.stream.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

public enum DelayMessageLevel implements BaseEnum {

    ONE(0,"1s"),

    TWO(1,"5s"),

    THREE(2,"10s"),

    FOUR(3,"30s"),

    FIVE(4,"1m"),

    SIX(5,"2m"),

    SEVEN(6,"3m"),

    EIGHT(7,"4m"),

    NINE(8,"5m"),

    TEN(9,"6m"),

    ELEVEN(10,"7m"),

    TWELVE(11,"8m"),

    THIRTEEN(12,"9m"),

    FOURTEEN(13,"10m"),

    FIFTEEN(14,"20m"),

    SIXTEEN(15,"30m"),

    SEVENTEEN(16,"1h"),

    EIGHTEEN(17,"2h"),

    ;

    DelayMessageLevel(Integer value,String desc) {

        this.value = value;

        this.desc = desc;

    }

    @Getter
    private final Integer value;

    @Getter
    private final String desc;

    @Override
    public String toString() {
        return value + "." + desc;
    }

}
