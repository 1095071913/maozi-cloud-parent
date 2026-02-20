package com.maozi.stream.config;

import com.maozi.common.BaseCommon;
import com.maozi.stream.enums.DelayMessageLevel;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import com.maozi.utils.context.ApplicationLinkContext;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageSendStream {

    @Resource
    private StreamBridge stream;

    @Resource
    private DiscoveryClient discoveryClient;

    public <D> Boolean sendMessage(String bindingName, Message<D> message){

        MessageBuilder<D> messageBuilder = MessageBuilder.fromMessage(message);

        List<ServiceInstance> instances = discoveryClient.getInstances(ApplicationEnvironmentContext.SERVICE_NAME);

        Map<String,List<ServiceInstance>> applicationClients = instances.stream().collect(Collectors.groupingBy((instance)-> instance.getMetadata().get(ApplicationLinkContext.NACOS_VERSION)));

        String version = BaseCommon.getVersionDefault(ApplicationLinkContext.VERSIONS.get());

        if(!ApplicationLinkContext.APPLICATION_DEFAULT_VERSION.equals(version) && BaseCommon.collectionIsEmpty(applicationClients.get(version))){
            version = ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
        }

        messageBuilder.setHeader("Gray", version);
        messageBuilder.setHeader(ApplicationLinkContext.VERSION, ApplicationLinkContext.VERSIONS.get());

        return stream.send(bindingName,messageBuilder.build());

    }

    public <D> Boolean sendMessage(String bindingName, D data){
        return sendMessage(bindingName,MessageBuilder.withPayload(data).build());
    }

    public <D> Boolean sendMessage(String bindingName, D data, String tags){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_TAGS, tags);

        return sendMessage(bindingName,messageBuilder.build());

    }

    public <D> Boolean sendMessage(String bindingName, D data, DelayMessageLevel level){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, level.getValue() + 1);

        return sendMessage(bindingName,messageBuilder.build());

    }

    public <D> Boolean sendMessage(String bindingName, D data,String tags,DelayMessageLevel level){

        MessageBuilder<D> messageBuilder = MessageBuilder.withPayload(data);

        messageBuilder.setHeader(MessageConst.PROPERTY_TAGS, tags);

        messageBuilder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, level.getValue() + 1);

        return sendMessage(bindingName,messageBuilder.build());

    }

}
