package com.jiumao.result.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.jiumao.factory.BaseResultFactory;
import com.jiumao.factory.result.AbstractBaseResult;
import com.jiumao.factory.result.code.CodeAttribute;
import com.jiumao.factory.result.code.CodeHashMap;
import com.jiumao.factory.result.error.ErrorResult;
import com.jiumao.tool.ApplicationEnvironmentConfig;
import com.jiumao.tool.MapperUtils;

import cn.hutool.core.util.ClassUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "【全局】枚举常量")
@RequestMapping("/application")
public class RestCode extends BaseResultFactory {

	private String dataId = "saas-code.json";

	private String group = "DEFAULT_GROUP";

	private Map<String, Object> enums = new HashMap<String, Object>();

	public RestCode() throws Exception {
		
		// 扫描该（参数）包名路径下的class文件
		ClassUtil.scanPackage("com.jiumao."+ApplicationEnvironmentConfig.applicationName.replace("jiumao-saas-", "")).stream()
				.forEach(item -> {
					if (item.isEnum()) { //判断该文件是否为枚举类
						Object[] enumConstants = item.getEnumConstants(); //获取枚举元素列表
						char[] charArray = item.getSimpleName().toCharArray(); //获取类名转数组
						charArray[0] += 32;
						enums.put(new String(charArray), enumConstants);
					}
				});
		// 获取nacos服务配置
		ConfigService configService = NacosFactory.createConfigService(ApplicationEnvironmentConfig.nacosAddr);
		// 获取静态错误编码数据
		setCode(configService.getConfig(dataId, group, 5000));
		// 添加监听器
		configService.addListener(dataId, group, new Listener() {
			@Override
			public void receiveConfigInfo(String codeJson) {
				setCode(codeJson);
			}

			@Override
			public Executor getExecutor() {
				return null;
			}
		});
	}

	public void setCode(String codeJson) {

		try {
			
			JSONObject codeMaps = JSONObject.parseObject(codeJson);
			Map<String, CodeHashMap> newCodeDatas = new HashMap<>();
			for(Object serviceCodeNameObject : codeMaps.keySet()) {
				
				String serviceCodeName=serviceCodeNameObject.toString();
				// 是否属于枚举类
				if(serviceCodeName.contains(ApplicationEnvironmentConfig.applicationName) && serviceCodeName.contains("enums")) {
					
					JSONObject serviceEnums = codeMaps.getJSONObject(serviceCodeName);
					for(Object serviceEnumKey:serviceEnums.keySet()) {
						enums.put(serviceEnumKey.toString(), serviceEnums.get(serviceEnumKey.toString()));
					}
					//是否属于同一返回类
				}else if(serviceCodeName.equals(BasicCode) || serviceCodeName.contains(ApplicationEnvironmentConfig.applicationName) ||  serviceCodeName.contains("jiumao-saas-gateway")){
					
					CodeHashMap codeHashMap = new CodeHashMap();
					JSONObject serviceCodes = codeMaps.getJSONObject(serviceCodeName);
					for(Object serviceCodeKey:serviceCodes.keySet()) {
						codeHashMap.put(MapperUtils.json2pojo(serviceCodes.get(serviceCodeKey.toString()).toString(), CodeAttribute.class));
					}
					newCodeDatas.put(serviceCodeName.toString(), codeHashMap);
					
				}
				
			}
			codeDatas = newCodeDatas;
			
		} catch (Exception e) {e.printStackTrace();}

	}

	@GetMapping("/getCode")
	@ApiOperation(value = "获取应用错误编码信息")
	public AbstractBaseResult<Map<String, CodeHashMap>> getCode() {
		return success(codeDatas);
	}

	@GetMapping("/getEnum/{name}")
	@ApiOperation(value = "获取应用枚举编码信息")
	public AbstractBaseResult getEnum(@PathVariable("name") String name) {
		return success(enums.get(name));
	}
	
	@GetMapping("/error")
	@ApiOperation(value = "测试失败",hidden = true)
	public ErrorResult error() {
		return error(code(500));
	}

}
