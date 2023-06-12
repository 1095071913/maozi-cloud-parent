package com.maozi.feign;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;

import com.maozi.common.result.error.exception.BusinessResultException;

import feign.Response;
import feign.Util;

@Component
public class ErrorDecoder implements feign.codec.ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
        	
            try {
            	return new BusinessResultException(Util.toString(response.body().asReader(Charset.defaultCharset())));
            } catch (IOException ex) {return ex;}
            
        }
        
}