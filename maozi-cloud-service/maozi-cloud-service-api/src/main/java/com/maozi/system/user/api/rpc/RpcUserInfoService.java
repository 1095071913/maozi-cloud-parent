package com.maozi.system.user.api.rpc;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.system.user.dto.SystemUser;

/**
 * 系统用户 RPC 服务接口
 * <p>
 * 定义系统用户信息相关的 RPC 远程调用方法。
 * </p>
 *
 * @author maozi
 */
public interface RpcUserInfoService {

	AbstractBaseResult<SystemUser> rpcGetById(Long id, String ... columns);

}
