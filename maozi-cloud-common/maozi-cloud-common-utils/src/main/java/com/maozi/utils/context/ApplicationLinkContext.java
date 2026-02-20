package com.maozi.utils.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;

@Data
public class ApplicationLinkContext {

    public static final String VERSION = "X-Version";

    public static final String USERNAME = "X-Username";

    public static final String NACOS_VERSION = "version";

    public static final String APPLICATION_DEFAULT_VERSION = "main";


    public static TransmittableThreadLocal<String> VERSIONS = new TransmittableThreadLocal<>();

    public static TransmittableThreadLocal<String> USERNAMES = new TransmittableThreadLocal<>();


    public static void clear(){

        VERSIONS.remove();

        USERNAMES.remove();

    }

}
