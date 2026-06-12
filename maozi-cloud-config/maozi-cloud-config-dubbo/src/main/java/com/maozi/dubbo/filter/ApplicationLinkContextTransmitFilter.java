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
 * 在 Dubbo 服务消费者端发起 RPC 调用时，将当前线程中的版本号和用户名
 * 从 {@link ApplicationLinkContext} 读取并写入 RPC 附件（Attachment）中，
 * 传递到服务提供者端，实现跨服务的链路上下文传播。
 * </p>
 * <p>
 * 与 {@link ApplicationLinkContextSetFilter} 配合使用：
 * <ul>
 *   <li>本过滤器在消费者端负责"写"：将上下文信息写入 RPC 附件</li>
 *   <li>{@link ApplicationLinkContextSetFilter} 在提供者端负责"读"：从 RPC 附件中提取上下文信息</li>
 * </ul>
 * </p>
 * <p>
 * 该过滤器通过 Dubbo SPI 机制加载，需在 resources/META-INF/dubbo 目录下配置。
 * </p>
 *
 * @author maozi
 * @see ApplicationLinkContext
 * @see ApplicationLinkContextSetFilter
 * @see Filter
 */
public class ApplicationLinkContextTransmitFilter implements Filter {

    /**
     * 拦截 Dubbo 消费端调用，将链路上下文信息写入 RPC 附件
     * <p>
     * 执行流程：
     * <ol>
     *   <li>获取 RPC 客户端附件对象</li>
     *   <li>将当前线程的版本号（version）写入附件，传递给服务提供者</li>
     *   <li>将当前线程的用户名（username）写入附件，传递给服务提供者</li>
     *   <li>执行实际的 Dubbo RPC 调用</li>
     * </ol>
     * </p>
     *
     * @param invoker    Dubbo 调用器，封装了被调用的远程服务信息
     * @param invocation 调用信息，包含方法名、参数类型、参数值等
     * @return 调用结果 {@link Result}，包含远程服务的返回值或异常信息
     * @throws RpcException RPC 调用异常
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        // 获取客户端 RPC 附件对象，用于向服务提供者传递附加信息
        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();

        // 将当前线程的版本号写入 RPC 附件，供服务提供者读取
        clientAttachment.setAttachment(ApplicationLinkContext.VERSION,ApplicationLinkContext.VERSIONS.get());

        // 将当前线程的用户名写入 RPC 附件，供服务提供者读取
        clientAttachment.setAttachment(ApplicationLinkContext.USERNAME, ApplicationLinkContext.USERNAMES.get());

        // 执行实际的 RPC 调用，附件信息会随请求一起发送到服务提供者
        return invoker.invoke(invocation);

    }

}
