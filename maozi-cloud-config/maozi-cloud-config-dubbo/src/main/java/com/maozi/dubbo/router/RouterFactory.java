package com.maozi.dubbo.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.CacheableRouterFactory;
import org.apache.dubbo.rpc.cluster.Router;

/**
 * 灰度路由工厂
 * <p>
 * 继承 Dubbo 的 {@link CacheableRouterFactory}，在消费者端自动激活，
 * 创建 {@link GrayRouter} 实例用于灰度发布路由。
 * 通过 Dubbo SPI 机制自动加载。
 * </p>
 *
 * @author maozi
 */
@Activate(group = CommonConstants.CONSUMER)
public class RouterFactory extends CacheableRouterFactory {

    /**
     * 创建灰度路由器实例
     *
     * @param url 服务 URL
     * @return 灰度路由器
     */
    @Override
    protected Router createRouter(URL url) {
        return new GrayRouter();
    }

}
