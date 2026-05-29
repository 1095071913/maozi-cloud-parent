package com.maozi.common;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author pengjinlong
 * @date 2026/4/25 01:37
 */
public class LogUtil {

    public final static ThreadLocal<StringBuilder> sqlLog = new ThreadLocal<>();

    public static void info(Logger log, String message){
        log(log,false,message);
    }

    public static void info(Logger log,Map<String, String> content){
        log(log,false,content);
    }

    public static void error(Logger log, String message){
        log(log,true,message);
    }

    public static void error(Logger log, Map<String, String> content){
        log(log,true,content);
    }

    public static void error(Logger log,Throwable e){
        log.error("",e);
    }

    public static void log(Logger log, Boolean error, String message){
        if(error) log.error(message);
        else log.info(message);
    }

    public static void log(Logger log,Boolean error,Map<String, String> content){
        String message = LogUtil.convertLog(content);
        log(log,error,message);
    }

    public static String convertLog(Map<String, String> content) {

        if(ObjectUtil.isNullEmpty(content)){
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (String key : content.keySet()) {
            sb.append("[ ").append(key).append("：").append(content.get(key)).append(" ]  ");
        }
        sb.delete(0, 2);
        sb.delete(sb.length() - 4, sb.length());

        return sb.toString();

    }

    public static String getStackTraceLog(Throwable t) {

        StringWriter sw = new StringWriter();

        PrintWriter pw = new PrintWriter(sw);

        t.printStackTrace(pw);

        return sw.toString();

    }

}
