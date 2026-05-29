package com.maozi.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlWrappers;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@Data
public class ApplicationLinkContext {

    public static final String VERSION = "X-Version";

    public static final String USERNAME = "X-Username";

    public static final String NACOS_VERSION = "version";

    public static final String APPLICATION_DEFAULT_VERSION = "main";


    public static TransmittableThreadLocal<String> VERSIONS = new TransmittableThreadLocal<>();

    public static TransmittableThreadLocal<String> USERNAMES = new TransmittableThreadLocal<>();


    public static String getVersionDefault(Object version){
        return ObjectUtil.isNotNullEmpty(version) && StringUtils.isNotBlank(version.toString()) ? version.toString() : ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
    }

    public static <T> Consumer<T> wrapConsumer(Consumer<T> consumer) {

        if (ObjectUtil.isNullEmpty(consumer)) {
            return null;
        }

        consumer = consumer.andThen((value)-> clearContext());

        return TtlWrappers.wrapConsumer(consumer);

    }

    public static void clearContext(){
        LogUtil.sqlLog.remove();
        VERSIONS.remove();
        USERNAMES.remove();
    }

}
