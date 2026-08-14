package com.maozi.stream.config;

import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.stream.enums.DelayMessageLevel;
import jakarta.annotation.Resource;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息发送工具
 * <p>
 * 基于 Spring Cloud Stream 的 {@link StreamBridge} 封装消息发送功能，
 * 支持灰度版本路由、消息标签（Tag）和延迟消息级别。
 * 自动从服务发现中获取灰度版本信息，在消息头中设置灰度标识。
 * </p>
 *
 * @author maozi
 */
@Component
public class MessageSendStream {

    /** Stream 桥接器，用于动态发送消息 */
    @Resource
    private StreamBridge stream;

    /** 服务发现客户端，用于获取灰度版本信息 */
    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 发送消息（完整参数）
     * <p>
     * 根据当前服务实例的版本信息，在消息头中设置灰度标识。
     * 若当前版本没有对应的服务实例，则回退到默认版本。
     * </p>
     *
     * @param bindingName 绑定名称
     * @param message 消息对象
     * @param <D> 消息数据类型
     * @return 是否发送成功
     */
    public <D> Boolean sendMessage(String bindingName, Message<D> message){

        MessageBuilder<D> messageBuilder = MessageBuilder.fromMessage(message);

        List<ServiceInstance> instances = discoveryClient.getInstances(ApplicationEnvironmentContext.SERVICE_NAME);

        Map<String,List<ServiceInstance>> applicationClients = instances.stream().collect(Collectors.groupingBy((instance)-> instance.getMetadata().get(ApplicationLinkContext.NACOS_VERSION_KEY)));

        String version = ApplicationLinkContext.getVersionDefault(ApplicationLinkContext.getVersion());

        if(!ApplicationLinkContext.APPLICATION_DEFAULT_VERSION.equals(version) && ObjectUtil.isNullEmpty(applicationClients.get(version))){
            version = ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
        }

        messageBuilder.setHeader("Gray", version);
        messageBuilder.setHeader(ApplicationLinkContext.VERSION_KEY, ApplicationLinkContext.getVersion());

        return stream.send(bindingName,messageBuilder.build());

    }

    /**
     * 发送消息（仅数据）
     *
     * @param bindingName 绑定名称
     * @param data 消息数据
     * @param <D> 数据类型
     * @return 是否发送成功
     */
    public <D> Boolean sendMessage(String bindingName, D data){
        return sendMessage(bindingName,MessageBuilder.withPayload(data).build());
    }

    /**
     * 发送消息（带 Tag 标签）
     *
     * @param bindingName 绑定名称
     * @param data 消息数据
     * @param tags RocketMQ 消息标签
     * @param <D> 数据类型
     * @return 是否发送成功
     */
    public <D> Boolean sendMessage(String bindingName, D data, String tags){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_TAGS, tags);

        return sendMessage(bindingName,messageBuilder.build());

    }

    /**
     * 发送延迟消息
     *
     * @param bindingName 绑定名称
     * @param data 消息数据
     * @param level 延迟消息级别
     * @param <D> 数据类型
     * @return 是否发送成功
     */
    public <D> Boolean sendMessage(String bindingName, D data, DelayMessageLevel level){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, level.getValue() + 1);

        return sendMessage(bindingName,messageBuilder.build());

    }

    /**
     * 发送延迟消息（带 Tag 标签）
     *
     * @param bindingName 绑定名称
     * @param data 消息数据
     * @param tags RocketMQ 消息标签
     * @param level 延迟消息级别
     * @param <D> 数据类型
     * @return 是否发送成功
     */
    public <D> Boolean sendMessage(String bindingName, D data,String tags,DelayMessageLevel level){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_TAGS, tags);

        messageBuilder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, level.getValue() + 1);

        return sendMessage(bindingName,messageBuilder.build());

    }

}
