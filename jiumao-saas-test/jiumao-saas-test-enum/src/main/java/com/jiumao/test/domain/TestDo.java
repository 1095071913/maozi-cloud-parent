//package com.jiumao.test.domain;
//
//import java.util.List;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
//import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
//import com.github.yulichang.annotation.EntityMapping;
//import com.jiumao.base.AbstractBaseDomain;
//import com.jiumao.test.enums.TestEnum;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@TableName("test") 
//public class TestDo extends AbstractBaseDomain{
//
//	@Column(value = "type_id" , type = MySqlTypeConstant.INT)
//	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
//	private TestEnum typeId;
//	
//    @TableField(exist = false)
//    @EntityMapping(joinField = "testId")
//    private List<TestDo2> testDo2;
//	
//}
