package com.maozi.lock.config;

import com.maozi.common.BaseCommon;
import com.maozi.lock.properties.LockProperties;
import io.netty.channel.nio.NioEventLoopGroup;
import javax.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Resource
    private LockProperties properties;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redisson() throws Exception {

        Config config = new Config();

        if(BaseCommon.isNotNull(properties.getNodeAddresses())){
            config.useClusterServers().setPassword(properties.getPassword()).addNodeAddress(properties.getNodeAddresses());
        }else {
            config.useSingleServer().setAddress(properties.getAddress()).setDatabase(properties.getDatabase()).setPassword(properties.getPassword());
        }

        config.setEventLoopGroup(new NioEventLoopGroup());

        return Redisson.create(config);

    }

}
