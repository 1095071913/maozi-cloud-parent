package com.maozi.oauth.config;

import com.maozi.base.CodeData;
import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.common.BaseCommon;
import com.maozi.utils.MapperUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class IRequestRejectedHandler extends BaseCommon implements RequestRejectedHandler {

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response,RequestRejectedException ex){
       CodeData<Void> errorCode = SystemErrorCode.MALICE_REQUEST_ERROR;
       MapperUtils.setResponseBody(response,error(errorCode,errorCode.getCode()).autoIdentifyHttpCode());
   }
   
}