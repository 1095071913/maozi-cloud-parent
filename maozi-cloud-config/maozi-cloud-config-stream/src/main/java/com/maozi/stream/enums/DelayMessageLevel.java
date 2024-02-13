package com.maozi.stream.enums;

import com.maozi.base.IEnum;
import lombok.Getter;
import lombok.Setter;

public enum DelayMessageLevel implements IEnum {

    One(0,"1s"),Two(1,"5s"),Three(2,"10s"),Four(3,"30s"),Five(4,"1m"),Six(5,"2m"),Seven(6,"3m"),Eight(7,"4m"),Nine(8,"5m"),Ten(9,"6m"),Eleven(10,"7m"),Twelve(11,"8m"),Thirteen(12,"9m"),Fourteen(13,"10m"),Fifteen(14,"20m"),Sixteen(15,"30m"),Seventeen(16,"1h"),Eighteen(17,"2h");

    DelayMessageLevel(Integer value,String desc) {

        this.value = value;

        this.desc = desc;

    }

    @Getter
    @Setter
    private Integer value;

    @Getter
    @Setter
    private String desc;

    @Override
    public String toString() {
        return value+"."+desc;
    }

}
