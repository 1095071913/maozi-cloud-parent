package com.maozi.lock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.lock")
public class LockProperties {

    private String address;

    private String password;

    private int database = 0;

    private String[] nodeAddresses;

    private long waitTime = 60;

    private long leaseTime = 60;

}