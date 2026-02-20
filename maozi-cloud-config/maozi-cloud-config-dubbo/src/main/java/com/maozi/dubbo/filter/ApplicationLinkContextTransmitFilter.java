package com.maozi.dubbo.filter;

import com.maozi.utils.context.ApplicationLinkContext;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcContextAttachment;
import org.apache.dubbo.rpc.RpcException;

public class ApplicationLinkContextTransmitFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();

        clientAttachment.setAttachment(ApplicationLinkContext.VERSION,ApplicationLinkContext.VERSIONS.get());

        clientAttachment.setAttachment(ApplicationLinkContext.USERNAME, ApplicationLinkContext.USERNAMES.get());

        return invoker.invoke(invocation);

//        return new AsyncRpcResult(CompletableFuture.completedFuture(new AppResponse(BaseCommon.error(new CodeData(6,"服务错误" + "(" + applicationName + ")"),500))), invocation);

    }

}