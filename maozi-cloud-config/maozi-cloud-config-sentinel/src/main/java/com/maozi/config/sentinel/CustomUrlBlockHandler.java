package com.maozi.config.sentinel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.maozi.common.BaseCommon;
import com.maozi.tool.MapperUtils;


public class CustomUrlBlockHandler extends BaseCommon implements BlockExceptionHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
		
		MapperUtils.getInstance().writeValue(response.getOutputStream(), error(code(601)).autoIdentifyHttpCode());    
	
	}
   
}