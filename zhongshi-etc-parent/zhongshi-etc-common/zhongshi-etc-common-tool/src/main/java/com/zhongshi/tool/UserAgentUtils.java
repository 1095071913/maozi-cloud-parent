package com.zhongshi.tool;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import javax.servlet.http.HttpServletRequest;

import com.zhongshi.dto.IpInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserAgentUtils {
	/**
	 * 获取用户代理
	 *
	 * @param request {@link HttpServletRequest}
	 * @return {@link UserAgent}
	 */
	public static UserAgent getUserAgent(HttpServletRequest request) {
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
	}

	/**
	 * 获取用户浏览器
	 *
	 * @param request {@link HttpServletRequest}
	 * @return {@link Browser}
	 */
	public static Browser getBrowser(HttpServletRequest request) {
		return getUserAgent(request).getBrowser();
	}

	/**
	 * 获取用户的 IP 地址
	 *
	 * @param request {@link HttpServletRequest}
	 * @return {@code String} 用户 IP 地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	/**
	 * 通过 IP 获取地址 (淘宝接口)
	 *
	 * @param ip {@code String} 用户 IP 地址
	 * @return {@code String} 用户地址
	 */
	public static IpInfo getIpInfo(String ip) {
		if ("127.0.0.1".equals(ip)) {
			ip = "127.0.0.1";
		}
		try {
			URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
			HttpURLConnection htpcon = (HttpURLConnection) url.openConnection();
			htpcon.setRequestMethod("GET");
			htpcon.setDoOutput(true);
			htpcon.setDoInput(true);
			htpcon.setUseCaches(false);
			InputStream in = htpcon.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			StringBuilder temp = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				temp.append(line).append("\r\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			return MapperUtils.json2pojoByTree(temp.toString(), "data", IpInfo.class);
		} catch (Exception e) {
			return new IpInfo();
		}
	}
}