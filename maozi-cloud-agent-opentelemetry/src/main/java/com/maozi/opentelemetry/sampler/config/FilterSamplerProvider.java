package com.maozi.opentelemetry.sampler.config;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSamplerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

/**
 * 自定义采样器 SPI 提供者
 * <p>
 * 通过 {@link AutoService} 自动注册到 OpenTelemetry 自动配置体系，
 * 将 {@link FilterSampler} 作为名为 {@value #PROVIDER_NAME} 的可配置采样器暴露，
 * 供 {@code otel.traces.sampler} 配置项按名称选用。
 * </p>
 *
 * @author pengjinlong
 */
@AutoService(ConfigurableSamplerProvider.class)
public class FilterSamplerProvider implements ConfigurableSamplerProvider {

    public static final String PROVIDER_NAME = "FilterSamplerProvider";

    @Override
    public Sampler createSampler(ConfigProperties configProperties) {
        return new FilterSampler();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

}