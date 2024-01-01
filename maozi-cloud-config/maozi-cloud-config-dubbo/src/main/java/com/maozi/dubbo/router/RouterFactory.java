package com.maozi.dubbo.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.CacheableRouterFactory;
import org.apache.dubbo.rpc.cluster.Router;

@Activate(group = CommonConstants.CONSUMER)
public class RouterFactory extends CacheableRouterFactory {

    @Override
    protected Router createRouter(URL url) {
        return new GrayRouter();
    }

}