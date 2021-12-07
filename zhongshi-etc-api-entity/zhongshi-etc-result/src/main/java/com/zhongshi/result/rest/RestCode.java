package com.zhongshi.result.rest;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api("Application-Code")
@RequestMapping("/application")
public class RestCode extends BaseResultFactory{

	static {

		code(new CodeHashMap() {

			{

				this.put(new CodeAttribute<String>(1, ""));

				this.put(new CodeAttribute<String>(2, "服务没有此code"));

				this.put(new CodeAttribute<String>(3, "Token错误"));

				this.put(new CodeAttribute<String>(6, "服务未启动"));

				this.put(new CodeAttribute<String>(7, "更新失败"));

				this.put(new CodeAttribute<String>(8, "添加失败"));

				this.put(new CodeAttribute<String>(9, "删除失败"));

				this.put(new CodeAttribute<String>(10, "分页参数错误"));

				this.put(new CodeAttribute<String>(11, "游客"));

				this.put(new CodeAttribute<String>(12, "验签失败"));

				this.put(new CodeAttribute<String>(13, "验证失败"));

				this.put(new CodeAttribute<String>(14, "字段全部为空"));

				this.put(new CodeAttribute<String>(16, "重复提交错误"));
				
				this.put(new CodeAttribute<String>(17, "未找到第三方接口转换器"));
				
				this.put(new CodeAttribute<String>(18, "未找到第三方数据请求转换器"));

				this.put(new CodeAttribute<String>(500, "内部服务错误"));
				
				this.put(new CodeAttribute<String>(400, "参数错误"));

				this.put(new CodeAttribute<String>(401, "用户未授权"));

				this.put(new CodeAttribute<String>(403, "权限不足"));

				this.put(new CodeAttribute<String>(404, "没有此资源"));
				
				this.put(new CodeAttribute<String>(405, "请求格式错误"));

				this.put(new CodeAttribute<String>(600, "错误熔断"));
				
				this.put(new CodeAttribute<String>(601, "限流中"));

				this.put(new CodeAttribute<String>(700, "网关错误"));

				this.put(new CodeAttribute<String>(800, "获取Token失败"));

			}

		});

	}

	@GetMapping("/getCode")
	@ApiOperation(value = "获取应用对应的code信息")
	public AbstractBaseResult<Map<String, CodeHashMap>> getCode() {
		return success(codeDatas);
	}

}
