package com.maozi.db.config;

import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.Log;

/**
 * MyBatis-Plus 自定义日志实现
 * <p>
 * 拦截 MyBatis-Plus 的 SQL 日志输出，将 SQL 语句和参数收集到线程变量中，
     * 用于在请求结束时统一记录完整的 SQL 日志。仅在应用运行中（IS_RUNNING=true）
     * 且包含 SQL 准备语句或参数信息时才进行收集。
 * </p>
 *
 * @author maozi
 */
@Slf4j
public class MybatisPlusLog implements Log {

    /**
     * 构造方法
     *
     * @param clazz 类名（MyBatis 内部传入）
     */
    public MybatisPlusLog(String clazz) {}

    /**
     * 是否启用 debug 级别
     *
     * @return 始终返回 true
     */
    public boolean isDebugEnabled() {return true;}

    /**
     * 是否启用 trace 级别
     *
     * @return 始终返回 true
     */
    public boolean isTraceEnabled() {return true;}

    /**
     * 记录 error 日志（带异常）
     *
     * @param s 日志内容
     * @param e 异常对象
     */
    public void error(String s, Throwable e) {log.error(s);}

    /**
     * 记录 error 日志
     *
     * @param s 日志内容
     */
    public void error(String s) {log.error(s); }

    /**
     * 收集 SQL 日志
     * <p>
     * 当应用已启动运行时，将 SQL 的 Preparing 和 Parameters 信息收集到线程变量中，
     * 并将参数值替换到 SQL 的占位符中，形成完整的可执行 SQL。
     * </p>
     *
     * @param s SQL 日志内容
     */
    public void debug(String s) {

        if(!ApplicationEnvironmentContext.IS_RUNNING){
            return;
        }

        StringBuilder sqlLog = LogUtil.sqlLog.get();

        if(ObjectUtil.isNullEmpty(sqlLog)) {

            sqlLog = new StringBuilder();

            LogUtil.sqlLog.set(sqlLog);

        }

        if(s.contains("==>  Preparing: ")) {

            s=s.replace("==>  Preparing: ","");

            sqlLog.append(s);

        }

        if(s.contains("==> Parameters: ")) {

            s=s.replace("==> Parameters: ","");

            if(StringUtils.isNotBlank(s)) {

                String [] params = s.split("\\),");

                for (String param : params) {

                    param = "'" + param.substring(0, param.indexOf("(")) + "'";

                    int index = sqlLog.indexOf("?");

                    if (index != -1) {
                        sqlLog.replace(index, index + 1, param);

                    }

                }

            }

            sqlLog.append(";");

        }

    }

    /**
     * trace 日志（空实现）
     *
     * @param s 日志内容
     */
    public void trace(String s) {}

    /**
     * warn 日志（空实现）
     *
     * @param s 日志内容
     */
    public void warn(String s) {}

}
