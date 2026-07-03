package com.maozi.opentelemetry.sampler.config;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSamplerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

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