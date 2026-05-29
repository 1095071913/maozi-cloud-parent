package com.maozi.oauth.config;

import com.maozi.common.ResultUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;

@Configuration
public class IRequestRejectedHandler implements RequestRejectedHandler {

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response,RequestRejectedException ex){
       ErrorCode errorCode = SystemErrorCode.MALICE_REQUEST_ERROR;
       WebUtil.writeResponseBody(response, ResultUtil.error(errorCode).autoIdentifyHttpCode(errorCode.getCode()));
   }
   
}