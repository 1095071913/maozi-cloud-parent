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
 * Dubbo 消费端链路上下文传递过滤器
 * <p>
 * 在 Dubbo 服务消费者端发起 RPC 调用时，将当前线程的版本号和用户名
 * 从 {@link ApplicationLinkContext} 写入 RPC 附件中，传递到服务提供者端，
 * 实现跨服务的链路上下文传播。
 * </p>
 *
 * @author maozi
 */
public class ApplicationLinkContextTransmitFilter implements Filter {

    /**
     * 拦截 Dubbo 调用，将链路上下文写入 RPC 附件后执行原始调用
     *
     * @param invoker Dubbo 调用器
     * @param invocation 调用信息
     * @return 调用结果
     * @throws RpcException RPC 调用异常
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();

        clientAttachment.setAttachment(ApplicationLinkContext.VERSION,ApplicationLinkContext.VERSIONS.get());

        clientAttachment.setAttachment(ApplicationLinkContext.USERNAME, ApplicationLinkContext.USERNAMES.get());

        return invoker.invoke(invocation);

    }

}
