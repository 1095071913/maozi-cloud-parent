package com.zhongshi.user;

import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhongshi.base.AbstractBaseDomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("test1")
@Builder
public class Test1Do extends AbstractBaseDomain implements Serializable{

	private String password;

}