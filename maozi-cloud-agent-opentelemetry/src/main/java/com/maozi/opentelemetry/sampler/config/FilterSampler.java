package com.maozi.opentelemetry.sampler.config;

import com.maozi.common.EnvironmentUtil;
import com.maozi.common.enums.EnvironmentType;
import com.maozi.opentelemetry.sampler.enums.FilterType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.List;

public class FilterSampler implements Sampler {

    private final List<String> EXCLUDED_TARGETS = List.of("NacosDiscoveryHeartBeatPublisher$$Lambda.run");

    @Override
    public String getDescription() {
        return FilterSamplerProvider.PROVIDER_NAME;
    }

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> parentLinks) {

        try{

            // 启动时屏蔽上报Sample
            if("main".equals(Thread.currentThread().getName())){
                return SamplingResult.create(SamplingDecision.DROP);
            }

            // 通过SpanName过滤
            if(EXCLUDED_TARGETS.contains(name)){
                return SamplingResult.create(SamplingDecision.DROP);
            }

            // 通过属性过滤
            if(!attributes.isEmpty()){
                for(FilterType type : FilterType.values()){

                    if(type.getHandle().filter(attributes)){
                        return SamplingResult.create(SamplingDecision.DROP);
                    }

                }
            }

//            System.err.println("name：" + name + "  ,attributes：" + attributes.asMap());

            return SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE);

        // 若异常则不过滤 且 新增异常Span提示
        }catch (Exception e){

            if(EnvironmentUtil.notEnvironment(EnvironmentType.PROD)){
                e.printStackTrace();
            }

            return SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE);

        }

    }

}
