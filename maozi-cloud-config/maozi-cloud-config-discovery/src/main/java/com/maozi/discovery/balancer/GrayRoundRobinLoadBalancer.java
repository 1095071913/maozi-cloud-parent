package com.maozi.discovery.balancer;

import com.google.common.collect.Lists;
import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.utils.context.ApplicationLinkContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequestContext;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GrayRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private final AtomicInteger position = new AtomicInteger(new Random().nextInt(1000));

    public GrayRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {

        HttpHeaders headers;

        Object requestContext = request.getContext();

        if(requestContext instanceof DefaultRequestContext defaultRequestContext){

            RequestData clientRequest = (RequestData)defaultRequestContext.getClientRequest();

            headers = clientRequest.getHeaders();

        }else{
            headers = (HttpHeaders) requestContext;
        }

        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);

        return supplier.get().next().map(list -> getInstanceResponse(list, headers));

    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {

        if(BaseCommon.collectionIsEmpty(instances)){
            throw new BusinessResultException(SystemErrorCode.SERVICE_NOT_EXIST_ERROR,404);
        }

        String version = headers.getFirst(ApplicationLinkContext.VERSION);

        List<ServiceInstance> mainApplicationClients = Lists.newArrayList();

        List<ServiceInstance> grayApplicationClients = Lists.newArrayList();

        instances.forEach(instance -> {

            String [] clientApplicationVersionSplit = instance.getMetadata().get(ApplicationLinkContext.NACOS_VERSION).split("-");

            String clientApplicationVersion = clientApplicationVersionSplit[clientApplicationVersionSplit.length - 1];

            if(BaseCommon.isNotEmpty(version) && version.equals(clientApplicationVersion)){
                grayApplicationClients.add(instance);
            }

            if(ApplicationLinkContext.APPLICATION_DEFAULT_VERSION.equals(clientApplicationVersion)){
                mainApplicationClients.add(instance);
            }

        });

        List<ServiceInstance> applicationClients = BaseCommon.collectionIsNotEmpty(grayApplicationClients) ? grayApplicationClients : mainApplicationClients;
        if(applicationClients.isEmpty()){
            throw new BusinessResultException(SystemErrorCode.SERVICE_NOT_EXIST_ERROR,404);
        }

        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;

        ServiceInstance instance = applicationClients.get(pos % applicationClients.size());

        return new DefaultResponse(instance);

    }

}