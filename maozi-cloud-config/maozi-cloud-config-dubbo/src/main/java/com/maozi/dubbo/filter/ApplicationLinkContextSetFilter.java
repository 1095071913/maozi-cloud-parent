package com.maozi.dubbo.filter;

import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationLinkContext;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcContextAttachment;
import org.apache.dubbo.rpc.RpcException;

public class ApplicationLinkContextSetFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();

        String version = serverAttachment.getAttachment(ApplicationLinkContext.VERSION);
        ApplicationLinkContext.VERSIONS.set(version);

        String username = serverAttachment.getAttachment(ApplicationLinkContext.USERNAME);
        ApplicationLinkContext.USERNAMES.set(username);

        try{return invoker.invoke(invocation);} finally{
            BaseCommon.clearContext();
        }

    }

}
