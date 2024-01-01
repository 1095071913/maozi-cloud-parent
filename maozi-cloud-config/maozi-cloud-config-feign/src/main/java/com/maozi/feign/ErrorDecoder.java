package com.maozi.feign;

import com.maozi.common.result.error.exception.BusinessResultException;
import feign.Response;
import feign.Util;
import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.stereotype.Component;

@Component
public class ErrorDecoder implements feign.codec.ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
        	
            try {return new BusinessResultException(response.status(),Util.toString(response.body().asReader(Charset.defaultCharset())),response.status());} catch (IOException e) {
                return e;
            }
            
        }
        
}