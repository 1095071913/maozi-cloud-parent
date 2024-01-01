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

        ApplicationLinkContext.set(serverAttachment.getAttachment("revision"),serverAttachment.getAttachment("username"));

        try{ return invoker.invoke(invocation); }catch (Throwable e){ throw e; }finally{
            BaseCommon.clearContext();
        }

    }

}
