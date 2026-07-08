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

/**
 * 自定义链路追踪采样器
 * <p>
 * 基于规则过滤噪声 Span，减少无用链路数据上报。过滤策略包括：
 * <ul>
 *   <li>启动阶段（main 线程）的 Span 丢弃，避免应用启动过程产生噪声</li>
 *   <li>Nacos 心跳等指定 Span 名称直接丢弃</li>
 *   <li>通过 {@link FilterType} 按 Span 属性过滤 HTTP（actuator/api-docs）、Redis（PING/AUTH 等）、MySQL（连接探活 SQL）等噪声请求</li>
 * </ul>
 * 未命中过滤规则的 Span 正常采样上报；过滤过程发生异常时为保证不丢数据，默认放行采样。
 * </p>
 *
 * @author pengjinlong
 */
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

        // 若异常则不过滤（放行采样），非生产环境打印堆栈便于排查
        }catch (Exception e){

            if(EnvironmentUtil.notEnvironment(EnvironmentType.PROD)){
                e.printStackTrace();
            }

            return SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE);

        }

    }

}
