package com.mountain.tool;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

public class LeafSnowflakeId {
	
	
	
    private static final String LEAF_HOST = "http://192.168.2.121:18080/api/snowflake/get/id";
    
    
    
    public static Long genId() {
        try {
            String string = Objects.requireNonNull(OkHttpClientUtil.getInstance().getData(LEAF_HOST).body()).string();
            return Long.valueOf(string);
        } catch (IOException e) {
            return 0L;
        }
    }
    
    
    public static void main(String[] args) throws ParseException {
        for (int i = 0; i < 100; i++) {
            System.out.println(LeafSnowflakeId.genId());
        }
    }
    
    
}