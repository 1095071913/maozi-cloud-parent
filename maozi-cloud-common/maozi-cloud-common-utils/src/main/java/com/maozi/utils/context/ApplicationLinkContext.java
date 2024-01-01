package com.maozi.utils.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;

@Data
public class ApplicationLinkContext {

    public static TransmittableThreadLocal<String> VERSIONS = new TransmittableThreadLocal();

    public static TransmittableThreadLocal<String> USERNAMES = new TransmittableThreadLocal();

    public static void set(String version,String username){

        VERSIONS.set(version);

        USERNAMES.set(username);

    }

    public static void clear(){

        VERSIONS.remove();

        USERNAMES.remove();

    }

}
