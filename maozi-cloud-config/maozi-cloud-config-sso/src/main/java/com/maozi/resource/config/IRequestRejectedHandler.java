package com.maozi.resource.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;

import com.maozi.common.BaseCommon;
import com.maozi.tool.MapperUtils;

@Configuration
public class IRequestRejectedHandler implements RequestRejectedHandler {
	
   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response,RequestRejectedException ex) throws IOException, ServletException {
      
	   response.setStatus(402);
	   
	   MapperUtils.getInstance().writeValue(response.getOutputStream(), BaseCommon.error(BaseCommon.baseCode(402),402).autoIdentifyHttpCode());
	   
   }
   
}