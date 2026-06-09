package com.maozi.lock.config;

import com.maozi.common.ObjectUtil;
import com.maozi.lock.properties.LockProperties;
import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 客户端配置类
 * <p>
 * 根据配置自动创建 Redisson 客户端实例，支持单节点和集群两种模式。
 * 使用 NIO 事件循环组提高网络通信性能。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class RedissonConfig {

    /** 分布式锁配置属性 */
    @Resource
    private LockProperties properties;

    /**
     * 创建 Redisson 客户端 Bean
     * <p>
     * 当配置了集群节点地址时使用集群模式，否则使用单节点模式。
     * </p>
     *
     * @return RedissonClient 实例
     * @throws Exception 配置异常
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redisson() throws Exception {

        Config config = new Config();

        if(ObjectUtil.isNotNullEmpty(properties.getNodeAddresses())){
            config.useClusterServers().setPassword(properties.getPassword()).addNodeAddress(properties.getNodeAddresses());
        }else {
            config.useSingleServer().setAddress(properties.getAddress()).setDatabase(properties.getDatabase()).setPassword(properties.getPassword());
        }

        config.setEventLoopGroup(new NioEventLoopGroup());

        return Redisson.create(config);

    }

}
