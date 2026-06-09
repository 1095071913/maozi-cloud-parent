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

package com.maozi.qny.api.impl;

import cn.hutool.core.io.FileUtil;
import com.maozi.common.CollectionUtil;
import com.maozi.common.JacksonUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.qny.api.QNYService;
import com.maozi.qny.properties.QNYProperties;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * 七牛云对象存储服务实现
 * <p>
 * 基于七牛云 Java SDK 实现图片和文件的上传、下载功能。
 * 自动管理上传凭证（Token），支持多种上传方式。
 * </p>
 *
 * @author maozi
 */
@Slf4j
public class QNYServiceImpl implements QNYService{

    /** 七牛云认证对象 */
	protected Auth auth;

    /** 七牛云上传管理器 */
	protected UploadManager uploadManager;

    /** 七牛云配置属性 */
	protected QNYProperties qnyProperties;

    /**
     * 构造方法，初始化七牛云客户端
     *
     * @param qnyProperties 七牛云配置属性
     */
	@Autowired
	public QNYServiceImpl(QNYProperties qnyProperties) {

		this.qnyProperties=qnyProperties;
		this.uploadManager = new UploadManager(new Configuration(Region.region2()));
		this.auth = Auth.create(qnyProperties.getAccessKey(), qnyProperties.getSecretKey());

	}

    /**
     * 批量上传图片（MultipartRequest 方式）
     *
     * @param files Multipart 请求
     * @return 上传后的文件路径列表
     */
	@Override
	public List<String> uploadImages(MultipartRequest files){
		if (!files.getMultiFileMap().isEmpty()){
			MultipartFile[] multipartFiles = files.getMultiFileMap().values().toArray(new MultipartFile[0]);
			try {
				List<String> images = uploadImages(multipartFiles);
				if (multipartFiles.length != images.size()){
					throw new BusinessResultException("上传图片数量异常");
				}
				return images;
			} catch (Exception e) {
				throw new BusinessResultException("上传图片失败:"+e.getMessage());
			}
		}
		return null;
	}

    /**
     * 批量上传图片
     *
     * @param files 图片文件数组
     * @return 上传后的文件路径列表
     * @throws Exception 上传异常
     */
	@Override
	public List<String> uploadImages(MultipartFile [] files) throws Exception{

		String upToken = auth.uploadToken(qnyProperties.getBucket());

		List<String> images = CollectionUtil.newArrayList();

		CollectionUtil.newArrayList(files).stream().forEach(file ->{
			try {images.add(uploadImage(file,upToken));} catch (Exception e) {
				LogUtil.error(log,e);
			}
		});
		return images;

	}

    /**
     * 上传单张图片
     *
     * @param file 图片文件
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	@Override
	public String uploadImage(MultipartFile file) throws Exception {
		return uploadImage(file,auth.uploadToken(qnyProperties.getBucket()));
	}

    /**
     * 上传文件（字节数组方式）
     *
     * @param bytes 文件字节数组
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	@Override
	public String uploadImage(byte[] bytes) throws Exception {
		return uploadImage(bytes,auth.uploadToken(qnyProperties.getBucket()));
	}

    /**
     * 根据 URL 下载图片
     *
     * @param url 图片 URL
     * @return 下载后的临时文件
     */
	@Override
	public File download(String url) {
		String downloadUrl = auth.privateDownloadUrl(url, 30000);
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder().url(downloadUrl).build();
		okhttp3.Response resp = null;
		try{
			resp = client.newCall(req).execute();
			if (resp.isSuccessful()) {
				ResponseBody body = resp.body();
				if (body.byteStream() == null){
					return null;
				}
				File tempFile = File.createTempFile(System.currentTimeMillis() + "_", "");
				FileUtil.writeFromStream(body.byteStream(),tempFile);
				return tempFile;
			}
		}catch (Exception e){
			log.error("下载文件失败",e);
		}
		return null;
	}

    /**
     * 上传文件（字节数组方式，指定 Token）
     *
     * @param bytes 文件字节数组
     * @param upToken 上传凭证
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	public String uploadImage(byte[] bytes,String upToken) throws Exception {
		Response response = uploadManager.put(new ByteArrayInputStream(bytes),null,upToken,null, null);

		DefaultPutRet putRet = JacksonUtil.jsonToObject(response.bodyString(), DefaultPutRet.class);

		return qnyProperties.getUrl()+putRet.key;
	}

    /**
     * 上传图片（MultipartFile 方式，指定 Token）
     *
     * @param file 图片文件
     * @param upToken 上传凭证
     * @return 上传后的文件路径
     * @throws Exception 上传异常
     */
	public String uploadImage(MultipartFile file,String upToken) throws Exception {

		byte[] bytes = file.getBytes();

		Response response = uploadManager.put(new ByteArrayInputStream(bytes),null,upToken,null, null);

		DefaultPutRet putRet = JacksonUtil.jsonToObject(response.bodyString(), DefaultPutRet.class);

		return qnyProperties.getUrl()+putRet.key;
	}

}
