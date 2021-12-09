package com.zhongshi.result.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import com.zhongshi.tool.ApplicationEnvironmentConfig;
import com.zhongshi.tool.MapperUtils;
import cn.hutool.core.util.ClassUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@RestController
@Api("Application-Code")
@RequestMapping("/application")
public class RestCode extends BaseResultFactory {

	private String dataId = "zhongshi-etc-code.json";

	private String group = "DEFAULT_GROUP";

	private Map<String, Object> enums = new HashMap<String, Object>();

	public RestCode() throws Exception {

		ClassUtil.scanPackage("com.zhongshi."+ ApplicationEnvironmentConfig.applicationName.replace("zhongshi-etc-", "") + ".enums")
				.forEach(item -> {
					if (item.isEnum()) {
						Object[] enumConstants = item.getEnumConstants();
						char[] charArray = item.getSimpleName().toCharArray();
						charArray[0] += 32;
						enums.put(new String(charArray), enumConstants);
					}
				});

		ConfigService configService = NacosFactory.createConfigService(ApplicationEnvironmentConfig.nacosAddr);
		setCode(configService.getConfig(dataId, group, 5000));
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
			
			JSONObject codeMaps = JSONObject.fromObject(codeJson);
			Map<String, CodeHashMap> newCodeDatas = new HashMap<>();
			for(Object serviceCodeNameObject : codeMaps.keySet()) {
				
				String serviceCodeName=serviceCodeNameObject.toString();
				
				if(serviceCodeName.contains("enums")) {
					
					JSONObject serviceEnums = codeMaps.getJSONObject(serviceCodeName.toString());
					for(Object serviceEnumKey:serviceEnums.keySet()) {
						enums.put(serviceEnumKey.toString(), serviceEnums.get(serviceEnumKey.toString()));
					}
					
				}else if(serviceCodeName.equals(BasicCode) || serviceCodeName.contains(ApplicationEnvironmentConfig.applicationName)){
					
					CodeHashMap codeHashMap = new CodeHashMap();
					JSONObject serviceCodes = codeMaps.getJSONObject(serviceCodeName.toString());
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
	public AbstractBaseResult<Map<String, CodeHashMap>> getEnum(@PathVariable("name") String name) {
		return success(enums.get(name));
	}

}
