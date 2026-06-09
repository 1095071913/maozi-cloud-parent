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

package com.maozi.qny.api;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.util.List;

/**
 * 七牛云对象存储服务接口
 * <p>
 * 定义图片和文件的上传、下载方法。
 * </p>
 *
 * @author maozi
 */
public interface QNYService {

    /**
     * 批量上传图片
     *
     * @param files 图片文件数组
     * @return 上传后的文件路径列表
     * @throws Exception 上传异常
     */
	List<String> uploadImages(MultipartFile [] files) throws Exception;

    /**
     * 上传单张图片
     *
     * @param file 图片文件
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	String uploadImage(MultipartFile file) throws Exception;

    /**
     * 上传图片（MultipartRequest 方式）
     *
     * @param files Multipart 请求
     * @return 上传后的文件路径列表
     */
	List<String> uploadImages(MultipartRequest files);

    /**
     * 上传文件（字节数组方式）
     *
     * @param bytes 文件字节数组
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	String uploadImage(byte[] bytes) throws Exception;

    /**
     * 根据 URL 下载图片
     *
     * @param url 图片 URL
     * @return 下载后的文件
     */
	File download(String url);

}
