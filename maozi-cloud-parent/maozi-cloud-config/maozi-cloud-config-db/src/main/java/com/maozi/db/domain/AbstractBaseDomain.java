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

package com.maozi.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.maozi.base.enums.Deleted;
import com.maozi.base.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础领域对象（Domain Object）抽象类。
 * <p>
 * 本类是所有数据库实体（DO，Data Object）的公共父类，封装了各实体通用的基础字段，
 * 包括主键ID、逻辑删除标志、业务状态和创建时间。子类只需继承本类即可自动获得这些字段，
 * 无需在每个实体中重复定义。
 * </p>
 *
 * <p><b>核心设计说明：</b></p>
 * <ul>
 *   <li>使用 MyBatis-Plus 的 {@link TableId} 注解标注主键，采用数据库自增（AUTO）策略生成ID</li>
 *   <li>使用 {@link TableLogic} 注解实现逻辑删除，即删除操作不会真正移除数据库记录，
 *       而是将 deleted 字段标记为已删除状态</li>
 *   <li>使用 {@link TableField#fill()} 配合 MyBatis-Plus 的自动填充机制，
 *       在插入数据时自动填充 deleted、status、createTime 等字段的值，
 *       避免在业务代码中手动赋值</li>
 * </ul>
 *
 * <p><b>类上的注解说明：</b></p>
 * <ul>
 *   <li>{@code @Data} —— Lombok 注解，自动生成 getter、setter、equals、hashCode、toString 方法</li>
 *   <li>{@code @NoArgsConstructor} —— Lombok 注解，自动生成无参构造函数</li>
 *   <li>{@code @AllArgsConstructor} —— Lombok 注解，自动生成全参构造函数</li>
 *   <li>{@code @SuperBuilder(toBuilder = true)} —— Lombok 注解，支持 Builder 模式构建对象，
 *       toBuilder=true 允许基于已有对象创建新的 Builder 进行修改</li>
 *   <li>{@code @JsonIgnoreProperties(ignoreUnknown = true)} —— Jackson 注解，
 *       反序列化时忽略 JSON 中存在但 Java 类中没有的未知属性，增强兼容性</li>
 * </ul>
 *
 * @author maozi
 * @see AbstractBaseNameDomain 在本类基础上扩展了 name 字段的子类
 * @see Deleted 逻辑删除枚举
 * @see Status 业务状态枚举
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseDomain implements Serializable {

	/**
	 * 序列化版本号，用于 Java 序列化机制保证版本一致性。
	 * 当类的结构发生变化时，应更新此版本号以避免反序列化失败。
	 */
	@Serial
    private static final long serialVersionUID = 1L;

	/**
	 * 主键ID，唯一标识数据库中的每一条记录。
	 * <p>
	 * 使用 {@link IdType#AUTO} 策略，表示主键由数据库自增生成的策略自动分配，
	 * 无需在应用层手动设置ID值。
	 * </p>
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 逻辑删除标志。
	 * <p>
	 * {@link TableLogic} 注解告诉 MyBatis-Plus 该字段为逻辑删除字段：
	 * <ul>
	 *   <li>执行 delete 操作时，MyBatis-Plus 会自动将 SQL 转换为 UPDATE 语句，
	 *       将此字段设置为"已删除"状态</li>
	 *   <li>执行 select 操作时，MyBatis-Plus 会自动追加 WHERE 条件，
	 *       过滤掉已删除的记录</li>
	 * </ul>
	 * {@link TableField#fill()} 配合 {@link FieldFill#INSERT} 表示该字段在插入时由
	 * MyBatis-Plus 的 MetaObjectHandler 自动填充默认值（通常为"未删除"状态）。
	 * </p>
	 *
	 * @see Deleted 逻辑删除枚举，定义了删除状态的取值
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	private Deleted deleted;

	/**
	 * 业务状态字段，用于控制记录的启用/禁用等状态。
	 * <p>
	 * {@link TableField#fill()} 配合 {@link FieldFill#INSERT} 表示该字段在插入时由
	 * MyBatis-Plus 的 MetaObjectHandler 自动填充默认值（通常为"启用"状态）。
	 * </p>
	 *
	 * @see Status 业务状态枚举，定义了启用、禁用等状态取值
	 */
	@TableField(fill = FieldFill.INSERT)
	private Status status;

	/**
	 * 记录创建时间，表示该条数据首次插入数据库的时间。
	 * <p>
	 * 使用 Java 8 的 {@link LocalDateTime} 类型精确到纳秒级别。
	 * {@link TableField#fill()} 配合 {@link FieldFill#INSERT} 表示该字段在插入时由
	 * MyBatis-Plus 的 MetaObjectHandler 自动填充当前时间，无需业务代码手动赋值。
	 * </p>
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

}
