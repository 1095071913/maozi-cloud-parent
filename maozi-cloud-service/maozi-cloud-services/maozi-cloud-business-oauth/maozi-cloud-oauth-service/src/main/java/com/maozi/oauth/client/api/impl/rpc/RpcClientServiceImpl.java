package com.maozi.oauth.client.api.impl.rpc;

import com.maozi.oauth.client.api.impl.ClientServiceImpl;
import com.maozi.oauth.client.api.rpc.RpcClientService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * OAuth2客户端Dubbo RPC服务实现类。
 * <p>
 * 继承 ClientServiceImpl 并实现 RpcClientService 接口，
 * 通过Dubbo RPC框架对外暴露客户端服务的远程调用能力。
 * 使用 @DubboService 注解标识为Dubbo服务提供者。
 * </p>
 */
@DubboService
public class RpcClientServiceImpl extends ClientServiceImpl implements RpcClientService {

}
