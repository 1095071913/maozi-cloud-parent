package com.maozi.system.redirect.util;

import com.maozi.common.ObjectUtil;
import com.maozi.common.WebUtil;
import com.maozi.system.redirect.enums.RedirectType;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author pengjinlong
 * @since 2026/8/14 13:01
 */
public class RedirectUtil {

    public static final String REDIRECT_KEY = "X-Redirect";

    public static void redirect(RedirectType redirectType){
        HttpServletResponse response = WebUtil.getResponse();
        if(ObjectUtil.isNotNullEmpty(response)){
            response.setHeader(REDIRECT_KEY,redirectType.getValue().toString());
        }
    }

}
