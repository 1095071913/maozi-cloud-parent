/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.maozi.ss.config;

import java.security.MessageDigest;

/**
 * 闪送签名工具配置
 * <p>
 * 提供 MD5 签名计算工具方法，用于闪送 API 请求签名的生成。
 * </p>
 *
 * @author maozi
 */
public class SSConfig {

    /**
     * 将字节数组转换为 MD5 大写十六进制字符串
     *
     * @param input 输入字节数组
     * @return 大写 MD5 十六进制字符串
     */
    public static String bytesToMD5(byte[] input) {
        String md5str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str.toUpperCase();
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 输入字节数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

}
