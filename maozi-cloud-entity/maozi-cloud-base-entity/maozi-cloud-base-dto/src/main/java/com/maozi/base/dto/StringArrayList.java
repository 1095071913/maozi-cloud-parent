package com.maozi.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * 字符串列表类型
 * <p>
 * 继承 {@link ArrayList}{@code <String>}，用于 JSON 反序列化时将数组自动映射为字符串列表，
 * 解决泛型擦除导致的类型丢失问题。
 * </p>
 *
 * @author maozi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringArrayList extends ArrayList<String> {

}
