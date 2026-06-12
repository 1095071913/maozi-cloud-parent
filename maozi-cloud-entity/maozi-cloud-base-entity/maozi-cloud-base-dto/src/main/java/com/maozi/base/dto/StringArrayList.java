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
 * <p>
 * 使用场景：当前端传入 JSON 字符串数组（如 ["a", "b", "c"]）作为接口参数时，
 * 由于 Java 泛型擦除，直接使用 {@code List<String>} 可能导致反序列化失败。
 * 通过继承具体的泛型类型，使 Jackson 能够正确推断出元素类型。
 * </p>
 *
 * @author maozi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringArrayList extends ArrayList<String> {

}
