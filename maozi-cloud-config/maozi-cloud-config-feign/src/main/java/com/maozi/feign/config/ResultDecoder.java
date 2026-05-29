package com.maozi.feign.config;

import com.maozi.common.JacksonUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.success.SuccessResult;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpMessageConverterExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResultDecoder implements Decoder {
	
	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
    	
    	Type dataType = ((ParameterizedType)type).getActualTypeArguments()[0];
    	
    	HttpMessageConverterExtractor<?> extractor = new HttpMessageConverterExtractor(dataType, this.messageConverters.getObject().getConverters());
    	
    	LinkedHashMap<String, Object> extractData = (LinkedHashMap<String, Object>) extractor.extractData(new FeignResponseAdapter(response));

		return type.getTypeName().contains(AbstractBaseResult.class.getName()) ? JacksonUtil.mapToObject(extractData, SuccessResult.class) : JacksonUtil.mapToObject(extractData, dataType);
    	
    }
    
    public final class FeignResponseAdapter implements ClientHttpResponse {

    	private final Response response;

		private FeignResponseAdapter(Response response) {
			this.response = response;
		}

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return HttpStatus.valueOf(this.response.status());
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return this.response.status();
		}

		@Override
		public String getStatusText() throws IOException {
			return this.response.reason();
		}

		@Override
		public void close() {
			try {
				this.response.body().close();
			}
			catch (IOException ex) {
				// Ignore exception on close...
			}
		}

		@Override
		public InputStream getBody() throws IOException {
			return this.response.body().asInputStream();
		}

		@Override
		public HttpHeaders getHeaders() {
			return getHttpHeaders(this.response.headers());
		}

	}
    
    static HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
			httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return httpHeaders;
	}
    
}