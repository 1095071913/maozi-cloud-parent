package com.jiumao.dingding;

import lombok.Data;


@Data
public class DingDingMessage {
	
	public DingDingMessage(String message) {
		text=new DingDingMessageInfo(message.toString());
	}
	
	private String msgtype = "text";
	
	private DingDingMessageInfo text;
	
	private At at=new At();
	
}

@Data
class DingDingMessageInfo {
	
	public DingDingMessageInfo(String content) {
		this.content=content;
	}
	
	private String content;
	
}

@Data
class At {
	
	private Boolean isAtAll = false;
	
}