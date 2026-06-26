package com.maozi.oauth.client.api.impl.rpc;

import com.maozi.oauth.client.api.impl.ClientServiceImpl;
import com.maozi.oauth.client.api.rpc.RpcClientService;
import com.maozi.service.api.annotation.RemoteService;

/**
 * OAuth2 客户端 Dubbo RPC 服务实现类
 * <p>
 * 继承 {@code ClientServiceImpl} 并实现 {@code RpcClientService} 接口，
 * 通过 {@code @RemoteService} 注解将客户端服务的本地能力对外暴露为 Dubbo RPC 调用。
 * </p>
 * <p>
 * 本类不重写任何方法，方法全部继承自 {@code ClientServiceImpl}；
 * 存在的意义仅是「让本地 Service 多实现一个 RPC 接口并注册到 Dubbo 注册中心」，
 * 属于典型的 RPC 适配器/暴露器角色。
 * </p>
 */
@RemoteService
public class RpcClientServiceImpl extends ClientServiceImpl implements RpcClientService {

}
