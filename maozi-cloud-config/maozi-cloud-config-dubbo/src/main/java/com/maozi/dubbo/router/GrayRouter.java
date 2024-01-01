package com.maozi.dubbo.router;

import com.google.common.collect.Lists;
import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationLinkContext;
import java.util.List;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;
import org.apache.dubbo.rpc.cluster.router.RouterResult;

public class GrayRouter extends AbstractRouter {

    @Override
    public <T> RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation, boolean needToPrintMessage) throws RpcException {

        String version = ApplicationLinkContext.VERSIONS.get();

        List<Invoker<T>> mainApplicationClients = Lists.newArrayList();

        List<Invoker<T>> grayApplicationClients = Lists.newArrayList();

        invokers.stream().forEach(invoker ->{

            String clientApplicationVersion = invoker.getUrl().getParameter("application.version");

            if(BaseCommon.isNotEmpty(version) && version.equals(clientApplicationVersion)){
                grayApplicationClients.add(invoker);
            }

            if("main".equals(clientApplicationVersion)){
                mainApplicationClients.add(invoker);
            }

        });

        return new RouterResult<>(BaseCommon.collectionIsNotEmpty(grayApplicationClients) ? grayApplicationClients : mainApplicationClients);

    }

}