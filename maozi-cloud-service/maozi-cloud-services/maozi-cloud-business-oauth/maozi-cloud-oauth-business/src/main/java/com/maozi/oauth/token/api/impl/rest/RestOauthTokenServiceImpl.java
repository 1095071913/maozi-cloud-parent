package com.maozi.oauth.token.api.impl.rest;

import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.api.impl.OauthTokenServiceImpl;
import com.maozi.oauth.token.api.rest.RestOauthTokenService;
import com.maozi.service.api.annotation.RestService;

import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/7/8 17:15
 */
@RestService
public class RestOauthTokenServiceImpl extends OauthTokenServiceImpl implements RestOauthTokenService {

    @Override
    public AbstractBaseResult<Map<String, Object>> restIntrospect(String token) {
        return ResultUtil.success(introspect(token));
    }

}
