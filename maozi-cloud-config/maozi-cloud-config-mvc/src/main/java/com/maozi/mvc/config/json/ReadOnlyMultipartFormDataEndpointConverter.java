package com.maozi.mvc.config.json;

import java.lang.reflect.Type;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadOnlyMultipartFormDataEndpointConverter extends MappingJackson2HttpMessageConverter {

    public ReadOnlyMultipartFormDataEndpointConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        // When a rest client(e.g. RestTemplate#getForObject) reads a request, 'RequestAttributes' can be null.
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) requestAttributes.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (handlerMethod == null) {
            return false;
        }
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return false;
        }
        // This converter reads data only when the mapped controller method consumes just 'MediaType.MULTIPART_FORM_DATA_VALUE'.
        if (requestMapping.consumes().length != 1|| !MediaType.MULTIPART_FORM_DATA_VALUE.equals(requestMapping.consumes()[0])) {
            return false;
        }
        return super.canRead(type, contextClass, mediaType);
    }

//      If you want to decide whether this converter can reads data depending on end point classes (i.e. classes with '@RestController'/'@Controller'),
//      you have to compare 'contextClass' to the type(s) of your end point class(es).
//      Use this 'canRead' method instead.
//      @Override
//      public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
//          return YourEndpointController.class == contextClass && super.canRead(type, contextClass, mediaType);
//      }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        // This converter is only be used for requests.
        return false;
    }
}