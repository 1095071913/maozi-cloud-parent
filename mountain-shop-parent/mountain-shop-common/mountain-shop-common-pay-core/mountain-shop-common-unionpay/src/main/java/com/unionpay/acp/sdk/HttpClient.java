/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       HTTP通信工具类
 * =============================================================================
 */
package com.unionpay.acp.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.unionpay.acp.sdk.BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier;
/**
 * 
 * @ClassName HttpClient
 * @Description acpsdk发送后台http请求类
 * @date 2016-7-22 下午4:03:25
 * 声明：以下代码只是为了方便接入方测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障
 */
public class HttpClient {
	/**
	 * 目标地址
	 */
	private URL url;

	/**
	 * 通信连接超时时间
	 */
	private int connectionTimeout;

	/**
	 * 通信读超时时间
	 */
	private int readTimeOut;

	/**
	 * 通信结果
	 */
	private String result;

	/**
	 * 获取通信结果
	 * @return
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 设置通信结果
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * 构造函数
	 * @param url 目标地址
	 * @param connectionTimeout HTTP连接超时时间
	 * @param readTimeOut HTTP读写超时时间
	 */
	public HttpClient(String url, int connectionTimeout, int readTimeOut) {
		try {
			this.url = new URL(url);
			this.connectionTimeout = connectionTimeout;
			this.readTimeOut = readTimeOut;
		} catch (MalformedURLException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
	}

	/**
	 * 发送信息到服务端
	 * @param data
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public int send(Map<String, String> data, String encoding) throws Exception {
		try {
			HttpURLConnection httpURLConnection = createConnection(encoding);
			if (null == httpURLConnection) {
				throw new Exception("Create httpURLConnection Failure");
			}
			String sendData = this.getRequestParamString(data, encoding);
			LogUtil.writeLog("请求报文(对每个报文域的值均已做url编码):[" + sendData + "]");
			this.requestServer(httpURLConnection, sendData,
					encoding);
			this.result = this.response(httpURLConnection, encoding);
			LogUtil.writeLog("Response message:[" + result + "]");
			return httpURLConnection.getResponseCode();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 发送信息到服务端 GET方式
	 * @param data
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public int sendGet(String encoding) throws Exception {
		try {
			HttpURLConnection httpURLConnection = createConnectionGet(encoding);
			if(null == httpURLConnection){
				throw new Exception("创建联接失败");
			}
			this.result = this.response(httpURLConnection, encoding);
			LogUtil.writeLog("同步返回报文:[" + result + "]");
			return httpURLConnection.getResponseCode();
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * HTTP Post发送消息
	 *
	 * @param connection
	 * @param message
	 * @throws IOException
	 */
	private void requestServer(final URLConnection connection, String message, String encoder)
			throws Exception {
		PrintStream out = null;
		try {
			connection.connect();
			out = new PrintStream(connection.getOutputStream(), false, encoder);
			out.print(message);
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}

	/**
	 * 显示Response消息
	 *
	 * @param connection
	 * @param CharsetName
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private String response(final HttpURLConnection connection, String encoding)
			throws URISyntaxException, IOException, Exception {
		InputStream in = null;
		StringBuilder sb = new StringBuilder(1024);
		BufferedReader br = null;
		try {
			if (200 == connection.getResponseCode()) {
				in = connection.getInputStream();
				sb.append(new String(read(in), encoding));
			} else {
				in = connection.getErrorStream();
				sb.append(new String(read(in), encoding));
			}
			LogUtil.writeLog("HTTP Return Status-Code:["
					+ connection.getResponseCode() + "]");
			return sb.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != br) {
				br.close();
			}
			if (null != in) {
				in.close();
			}
			if (null != connection) {
				connection.disconnect();
			}
		}
	}
	
	public static byte[] read(InputStream in) throws IOException {
		byte[] buf = new byte[1024];
		int length = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((length = in.read(buf, 0, buf.length)) > 0) {
			bout.write(buf, 0, length);
		}
		bout.flush();
		return bout.toByteArray();
	}
	
	/**
	 * 创建连接
	 *
	 * @return
	 * @throws ProtocolException
	 */
	private HttpURLConnection createConnection(String encoding) throws ProtocolException {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
			return null;
		}
		httpURLConnection.setConnectTimeout(this.connectionTimeout);// 连接超时时间
		httpURLConnection.setReadTimeout(this.readTimeOut);// 读取结果超时时间
		httpURLConnection.setDoInput(true); // 可读
		httpURLConnection.setDoOutput(true); // 可写
		httpURLConnection.setUseCaches(false);// 取消缓存
		httpURLConnection.setRequestProperty("Content-type",
				"application/x-www-form-urlencoded;charset=" + encoding);
		httpURLConnection.setRequestMethod("POST");
		if ("https".equalsIgnoreCase(url.getProtocol())) {
			HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
			//是否验证https证书，测试环境请设置false，生产环境建议优先尝试true，不行再false
			if(!SDKConfig.getConfig().isIfValidateRemoteCert()){
				husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
				husn.setHostnameVerifier(new TrustAnyHostnameVerifier());//解决由于服务器证书问题导致HTTPS无法访问的情况
			}
			return husn;
		}
		return httpURLConnection;
	}

	/**
	 * 创建连接
	 *
	 * @return
	 * @throws ProtocolException
	 */
	private HttpURLConnection createConnectionGet(String encoding) throws ProtocolException {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
			return null;
		}
		httpURLConnection.setConnectTimeout(this.connectionTimeout);// 连接超时时间
		httpURLConnection.setReadTimeout(this.readTimeOut);// 读取结果超时时间
		httpURLConnection.setUseCaches(false);// 取消缓存
		httpURLConnection.setRequestProperty("Content-type",
				"application/x-www-form-urlencoded;charset=" + encoding);
		httpURLConnection.setRequestMethod("GET");
		if ("https".equalsIgnoreCase(url.getProtocol())) {
			HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
			//是否验证https证书，测试环境请设置false，生产环境建议优先尝试true，不行再false
			if(!SDKConfig.getConfig().isIfValidateRemoteCert()){
				husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
				husn.setHostnameVerifier(new TrustAnyHostnameVerifier());//解决由于服务器证书问题导致HTTPS无法访问的情况
			}
			return husn;
		}
		return httpURLConnection;
	}
	
	/**
	 * 将Map存储的对象，转换为key=value&key=value的字符
	 *
	 * @param requestParam
	 * @param coder
	 * @return
	 */
	private String getRequestParamString(Map<String, String> requestParam, String coder) {
		if (null == coder || "".equals(coder)) {
			coder = "UTF-8";
		}
		StringBuffer sf = new StringBuffer("");
		String reqstr = "";
		if (null != requestParam && 0 != requestParam.size()) {
			for (Entry<String, String> en : requestParam.entrySet()) {
				try {
					sf.append(en.getKey()
							+ "="
							+ (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
									.encode(en.getValue(), coder)) + "&");
				} catch (UnsupportedEncodingException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
					return "";
				}
			}
			reqstr = sf.substring(0, sf.length() - 1);
		}
		LogUtil.writeLog("Request Message:[" + reqstr + "]");
		return reqstr;
	}

}
