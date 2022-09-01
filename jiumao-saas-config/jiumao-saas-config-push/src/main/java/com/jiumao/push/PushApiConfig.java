package com.jiumao.push;


import com.getui.push.v2.sdk.ApiHelper;
import com.getui.push.v2.sdk.GtApiConfiguration;
import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.api.UserApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PushApiConfig {


    private ApiHelper apiHelper;

    public PushApiConfig( @Value("${push.config.baseUrl}") String baseUrl,@Value("${push.config.appId}") String appId
            ,@Value("${push.config.appKey}") String appKey,@Value("${push.config.masterSecret}") String masterSecret){
        GtApiConfiguration apiConfiguration = new GtApiConfiguration();
        //填写应用配置，参数在“Uni Push”下的“应用配置”页面中获取
        apiConfiguration.setAppId(appId);
        apiConfiguration.setAppKey(appKey);
        apiConfiguration.setMasterSecret(masterSecret);
        apiConfiguration.setDomain(baseUrl);
        // 实例化ApiHelper对象，用于创建接口对象
        apiHelper = ApiHelper.build(apiConfiguration);
    }

    @Bean(name = "myUserApi")
    public UserApi userApi(){
        UserApi userApi = apiHelper.creatApi(UserApi.class);
        return userApi;
    }

    @Bean(name = "myPushApi")
    public PushApi pushApi() {

        // 创建对象，建议复用。目前有PushApi、StatisticApi、UserApi
        PushApi pushApi = apiHelper.creatApi(PushApi.class);

        return pushApi;
    }

}
