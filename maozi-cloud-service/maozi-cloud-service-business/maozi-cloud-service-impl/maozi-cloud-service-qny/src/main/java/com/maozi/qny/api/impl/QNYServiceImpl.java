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
import com.google.common.collect.Lists;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.qny.api.QNYService;
import com.maozi.qny.properties.QNYProperties;
import com.maozi.utils.MapperUtils;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

/**	
 * 
 *  Specifications：功能
 * 
 *  Author：彭晋龙
 * 
 *  Creation Date：2021-12-18:16:32:34
 *
 *  Copyright Ownership：xiao mao zi
 * 
 *  Agreement That：Apache 2.0
 * 
 */

public class QNYServiceImpl extends BaseCommon implements QNYService{
	
	protected Auth auth;
	
	protected UploadManager uploadManager;
	
	protected QNYProperties qnyProperties;
	
	@Autowired
	public QNYServiceImpl(QNYProperties qnyProperties) {
	
		this.qnyProperties=qnyProperties;
		this.uploadManager = new UploadManager(new Configuration(Region.region2())); 
		this.auth = Auth.create(qnyProperties.getAccessKey(), qnyProperties.getSecretKey());
	
	}


	@Override
	public List<String> uploadImages(MultipartRequest files){
		if (files.getMultiFileMap().size() > 0){
			MultipartFile[] multipartFiles = files.getMultiFileMap().values().toArray(new MultipartFile[files.getMultiFileMap().values().size()]);
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

	@Override
	public List<String> uploadImages(MultipartFile [] files) throws Exception{
		
		String upToken = auth.uploadToken(qnyProperties.getBucket());
		
		List<String> images = Lists.newArrayList();
		
		Lists.newArrayList(files).stream().forEach(file ->{
			try {images.add(uploadImage(file,upToken));} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return images;
		
	}

	@Override
	public String uploadImage(MultipartFile file) throws Exception {
		return uploadImage(file,auth.uploadToken(qnyProperties.getBucket()));
	}

	@Override
	public String uploadImage(byte[] bytes) throws Exception {
		return uploadImage(bytes,auth.uploadToken(qnyProperties.getBucket()));
	}

	@Override
	public File download(String url) {
		// 获取下载签名
		String downloadUrl = auth.privateDownloadUrl(url, 30000);
		//下载
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

	public String uploadImage(byte[] bytes,String upToken) throws Exception {
		Response response = uploadManager.put(new ByteArrayInputStream(bytes),null,upToken,null, null);

		DefaultPutRet putRet = MapperUtils.jsonToPojo(response.bodyString(), DefaultPutRet.class);

		return qnyProperties.getUrl()+putRet.key;
	}

	public String uploadImage(MultipartFile file,String upToken) throws Exception {
		
		byte[] bytes = file.getBytes();
		
		Response response = uploadManager.put(new ByteArrayInputStream(bytes),null,upToken,null, null);
		
		DefaultPutRet putRet = MapperUtils.jsonToPojo(response.bodyString(), DefaultPutRet.class);
		
		return qnyProperties.getUrl()+putRet.key;
	}

}
