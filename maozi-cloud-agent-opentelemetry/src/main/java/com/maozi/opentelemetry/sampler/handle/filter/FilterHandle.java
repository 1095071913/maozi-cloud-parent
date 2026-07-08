package com.maozi.opentelemetry.sampler.handle.filter;

import io.opentelemetry.api.common.Attributes;
import java.util.List;

/**
 * Span 属性过滤器抽象基类
 * <p>
 * 定义按 Span 属性（{@link Attributes}）判断是否需要丢弃该 Span 的统一契约。
 * 子类按来源（HTTP、Redis、MySQL 等）实现具体的过滤规则。
 * </p>
 *
 * @author pengjinlong
 */
public abstract class FilterHandle {

     /**
      * 返回该过滤器关注的来源标识列表（如 "redis"、"mysql"），用于匹配 Span 属性中的来源字段。
      *
      * @return 来源标识列表
      */
     public abstract List<String> getNames();

     /**
      * 根据 Span 属性判断该 Span 是否需要被过滤（丢弃）。
      *
      * @param attributes Span 携带的属性集合
      * @return {@code true} 表示该 Span 命中过滤规则应丢弃；{@code false} 表示保留
      */
     public abstract Boolean filter(Attributes attributes);

}
