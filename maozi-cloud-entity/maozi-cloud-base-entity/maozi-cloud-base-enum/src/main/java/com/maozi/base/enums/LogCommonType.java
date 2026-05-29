package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

public enum LogCommonType implements BaseEnum {
    
        GATEWAY(0,"Gateway"),
    
        WEB(1,"Web"),
    
        RPC(2,"Rpc"),
    
        JOB(3,"Job"),
    
        MQ(4,"MQ"),
    
        WEB_SOCKET(5,"WebSocket"),
    
        ;
    
        LogCommonType(Integer value,String desc) {
    
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