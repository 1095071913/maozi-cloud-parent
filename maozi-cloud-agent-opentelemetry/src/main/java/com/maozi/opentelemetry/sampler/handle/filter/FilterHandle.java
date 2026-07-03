package com.maozi.opentelemetry.sampler.handle.filter;

import io.opentelemetry.api.common.Attributes;
import java.util.List;

/**
 * @author pengjinlong
 */
public abstract class FilterHandle {

     public abstract List<String> getNames();

     public abstract Boolean filter(Attributes attributes);

}
