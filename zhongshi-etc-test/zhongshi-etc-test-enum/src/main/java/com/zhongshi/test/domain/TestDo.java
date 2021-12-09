package com.zhongshi.test.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.zhongshi.base.AbstractBaseDomain;
import com.zhongshi.test.enums.TestEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("test") 
public class TestDo extends AbstractBaseDomain{

	@Column(value = "type_id" , type = MySqlTypeConstant.INT)
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	private TestEnum typeId;
	
	@TableField(select = false)
	private String test1;
	
}
