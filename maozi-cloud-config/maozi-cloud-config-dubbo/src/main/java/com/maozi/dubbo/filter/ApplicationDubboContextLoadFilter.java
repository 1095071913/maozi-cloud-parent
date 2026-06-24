package com.maozi.dubbo.filter;

import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
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
 * 在 Dubbo 服务提供者端接收请求时，从 RPC 附件（Attachment）中提取消费者传递过来的
 * 版本号和用户名等链路上下文信息，设置到当前线程的 {@link ApplicationLinkContext} 中，
 * 以便后续业务逻辑可以通过 {@link ApplicationLinkContext} 获取调用方的上下文信息。
 * </p>
 * <p>
 * 请求处理完成后，在 finally 块中自动清理线程上下文，
 * 防止 Dubbo 线程池复用时上下文数据泄漏到其他请求。
 * </p>
 * <p>
 * 该过滤器通过 Dubbo SPI 机制加载，需在 resources/META-INF/dubbo 目录下配置。
 * </p>
 *
 * @author maozi
 * @see ApplicationLinkContext
 * @see Filter
 */
public class ApplicationDubboContextLoadFilter implements Filter {

    /**
     * 拦截 Dubbo 服务端调用，提取并设置链路上下文信息
     * <p>
     * 执行流程：
     * <ol>
     *   <li>从 RPC 服务端附件中获取版本号（version），设置到线程变量中</li>
     *   <li>从 RPC 服务端附件中获取用户名（username），设置到线程变量中</li>
     *   <li>执行实际的 Dubbo 调用</li>
     *   <li>在 finally 块中清理线程上下文，防止数据泄漏</li>
     * </ol>
     * </p>
     *
     * @param invoker    Dubbo 调用器，封装了被调用的服务信息
     * @param invocation 调用信息，包含方法名、参数类型、参数值等
     * @return 调用结果 {@link Result}，包含返回值或异常信息
     * @throws RpcException RPC 调用异常
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        // 获取服务端的 RPC 附件对象，用于读取消费者传递的附件信息
        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();

        // 从 RPC 附件中提取版本号，设置到当前线程的 ApplicationLinkContext 中
        String version = serverAttachment.getAttachment(ApplicationLinkContext.VERSION_KEY);
        ApplicationLinkContext.versions.set(version);

        // 从 RPC 附件中提取用户名，设置到当前线程的 ApplicationLinkContext 中
        String currentUserInfoJson = serverAttachment.getAttachment(ApplicationLinkContext.CURRENT_USER_INFO_KEY);
        if(ObjectUtil.isNotNullEmpty(currentUserInfoJson)){
            ApplicationLinkContext.currentUserInfos.set(JacksonUtil.jsonToObject(currentUserInfoJson, CurrentUserInfo.class));
        }

        // 执行实际调用，在 finally 中清理上下文，确保即使发生异常也不会造成线程数据泄漏
        try{return invoker.invoke(invocation);} finally{
            ApplicationLinkContext.clearContext();
        }

    }

}
