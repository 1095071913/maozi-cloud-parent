package com.maozi.oauth.client.api.impl.rpc;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.client.api.impl.ClientServiceImpl;
import com.maozi.oauth.client.api.rpc.RpcClientService;
import com.maozi.oauth.client.dto.ClientDto;
import com.maozi.oauth.client.param.ClientSaveUpdateParam;
import com.maozi.service.api.annotation.RemoteService;

/**
 * OAuth2 客户端 Dubbo RPC 服务实现类
 * <p>
 * 继承 {@code ClientServiceImpl} 并实现 {@code RpcClientService} 接口，
 * 通过 {@code @RemoteService} 注解将客户端服务的本地能力对外暴露为 Dubbo RPC 调用。
 * </p>
 * <p>
 * 除重写供AI智能体调用的 {@code rpcAiSave} 方法外，其余方法全部继承自
 * {@code ClientServiceImpl}；存在的意义主要是「让本地 Service 多实现一个
 * RPC 接口并注册到 Dubbo 注册中心」，属于典型的 RPC 适配器/暴露器角色。
 * </p>
 */
@RemoteService
public class RpcClientServiceImpl extends ClientServiceImpl implements RpcClientService {

    /**
     * 新增客户端并返回保存后的客户端信息
     * <p>
     * 供AI智能体等远程调用方使用：先以新增方式保存客户端，
     * 再根据保存后的主键ID查询并封装为客户端DTO返回。
     * </p>
     *
     * @param param 客户端保存参数
     * @return 包含新增客户端信息的统一响应结果
     */
    @Override
    public AbstractBaseResult<ClientDto> rpcAiSave(ClientSaveUpdateParam param) {
        Long clientId = restSaveUpdate(null, param);
        return getByIdResult(clientId);
    }

}
