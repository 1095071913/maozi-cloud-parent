/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maozi.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * 带名称的基础领域对象（Domain Object）抽象类。
 * <p>
 * 本类继承自 {@link AbstractBaseDomain}，在基础字段（主键ID、逻辑删除、状态、创建时间）之上
 * 扩展了一个通用的 {@code name} 字段。适用于所有需要"名称"属性的实体对象，
 * 例如：角色名称、权限名称、分类名称、配置项名称等。
 * </p>
 *
 * <p><b>继承关系说明：</b></p>
 * <pre>
 *   Serializable（接口）
 *       └── AbstractBaseDomain（基础领域对象，包含 id、deleted、status、createTime）
 *               └── AbstractBaseNameDomain（本类，扩展 name 字段）
 *                       └── 具体业务实体类（如角色、权限等）
 * </pre>
 *
 * <p><b>类上的注解说明：</b></p>
 * <ul>
 *   <li>{@code @Data} —— Lombok 注解，自动生成 getter、setter、equals、hashCode、toString 方法</li>
 *   <li>{@code @NoArgsConstructor} —— Lombok 注解，自动生成无参构造函数</li>
 *   <li>{@code @AllArgsConstructor} —— Lombok 注解，自动生成全参构造函数（包含父类字段）</li>
 *   <li>{@code @SuperBuilder(toBuilder = true)} —— Lombok 注解，支持 Builder 模式构建对象，
 *       配合父类的 {@code @SuperBuilder} 注解，可以在 Builder 中同时设置父类和本类的字段；
 *       toBuilder=true 允许基于已有对象创建新的 Builder 进行修改</li>
 *   <li>{@code @EqualsAndHashCode(callSuper = true)} —— Lombok 注解，
 *       生成 equals 和 hashCode 方法时纳入父类（{@link AbstractBaseDomain}）的字段，
 *       确保基于完整属性进行比较，避免因忽略父类字段而导致逻辑错误</li>
 *   <li>{@code @JsonIgnoreProperties(ignoreUnknown = true)} —— Jackson 注解，
 *       反序列化时忽略 JSON 中存在但 Java 类中没有的未知属性，增强兼容性</li>
 * </ul>
 *
 * @author maozi
 * @see AbstractBaseDomain 本类的父类，定义了 id、deleted、status、createTime 等基础字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseNameDomain extends AbstractBaseDomain {

	/**
	 * 序列化版本号，用于 Java 序列化机制保证版本一致性。
	 * 当类的结构发生变化时，应更新此版本号以避免反序列化失败。
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 名称字段，用于存储实体的显示名称。
	 * <p>
	 * 例如：
	 * <ul>
	 *   <li>角色实体中存储角色名称（如"管理员"、"普通用户"）</li>
	 *   <li>权限实体中存储权限名称（如"用户查看"、"用户编辑"）</li>
	 *   <li>分类实体中存储分类名称（如"电子产品"、"服装"）</li>
	 * </ul>
	 * </p>
	 */
	private String name;

}
