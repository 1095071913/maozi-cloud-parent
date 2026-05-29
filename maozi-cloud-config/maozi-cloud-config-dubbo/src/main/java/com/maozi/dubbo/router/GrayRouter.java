package com.maozi.dubbo.router;

import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;
import org.apache.dubbo.rpc.cluster.router.RouterResult;

import java.util.List;

public class GrayRouter extends AbstractRouter {

    @Override
    public <T> RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation, boolean needToPrintMessage) throws RpcException {

        String version = ApplicationLinkContext.VERSIONS.get();

        List<Invoker<T>> mainApplicationClients = CollectionUtil.newArrayList();

        List<Invoker<T>> grayApplicationClients = CollectionUtil.newArrayList();

        invokers.forEach(invoker ->{

            String clientApplicationVersion = invoker.getUrl().getParameter("application.version");

            if(StringUtils.isNotBlank(version) && version.equals(clientApplicationVersion)){
                grayApplicationClients.add(invoker);
            }

            if(ApplicationLinkContext.APPLICATION_DEFAULT_VERSION.equals(clientApplicationVersion)){
                mainApplicationClients.add(invoker);
            }

        });

        return new RouterResult<>(ObjectUtil.isNotNullEmpty(grayApplicationClients) ? grayApplicationClients : mainApplicationClients);

    }

}