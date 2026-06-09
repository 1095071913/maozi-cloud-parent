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

/**
 * 灰度路由器
 * <p>
 * 基于 Dubbo 的 {@link AbstractRouter} 实现灰度发布路由策略。
 * 根据当前请求的版本号将流量路由到匹配版本的服务实例（灰度节点），
 * 若无匹配的灰度节点则路由到主版本节点。
 * </p>
 *
 * @author maozi
 */
public class GrayRouter extends AbstractRouter {

    /**
     * 根据版本号进行灰度路由
     * <p>
     * 将服务实例分为灰度节点和主版本节点两组：
     * <ul>
     *   <li>灰度节点：{@code application.version} 与当前请求版本匹配的实例</li>
     *   <li>主版本节点：{@code application.version} 等于默认版本的实例</li>
     * </ul>
     * 优先返回灰度节点列表，若无灰度节点则返回主版本节点列表。
     * </p>
     *
     * @param invokers 可用的服务调用者列表
     * @param url 服务 URL
     * @param invocation 调用信息
     * @param needToPrintMessage 是否需要打印路由信息
     * @return 路由结果
     * @throws RpcException RPC 调用异常
     */
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
