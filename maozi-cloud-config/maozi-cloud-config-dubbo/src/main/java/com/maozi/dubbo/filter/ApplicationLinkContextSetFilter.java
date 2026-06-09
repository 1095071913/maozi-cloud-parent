package com.maozi.dubbo.filter;

import com.maozi.common.context.ApplicationLinkContext;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcContextAttachment;
import org.apache.dubbo.rpc.RpcException;

/**
 * Dubbo 服务端链路上下文设置过滤器
 * <p>
 * 在 Dubbo 服务提供者端接收请求时，从 RPC 附件中提取版本号和用户名，
 * 设置到当前线程的 {@link ApplicationLinkContext} 中。
 * 请求处理完成后自动清理上下文，防止线程池复用导致的数据泄漏。
 * </p>
 *
 * @author maozi
 */
public class ApplicationLinkContextSetFilter implements Filter {

    /**
     * 拦截 Dubbo 调用，设置链路上下文并执行原始调用
     *
     * @param invoker Dubbo 调用器
     * @param invocation 调用信息
     * @return 调用结果
     * @throws RpcException RPC 调用异常
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();

        String version = serverAttachment.getAttachment(ApplicationLinkContext.VERSION);
        ApplicationLinkContext.VERSIONS.set(version);

        String username = serverAttachment.getAttachment(ApplicationLinkContext.USERNAME);
        ApplicationLinkContext.USERNAMES.set(username);

        try{return invoker.invoke(invocation);} finally{
            ApplicationLinkContext.clearContext();
        }

    }

}
