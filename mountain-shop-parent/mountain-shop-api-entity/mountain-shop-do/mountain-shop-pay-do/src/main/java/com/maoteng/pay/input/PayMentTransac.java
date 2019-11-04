package com.maoteng.pay.input;
import com.mountain.base.AbstractBaseDomain;
import lombok.Data;

@Data
public class PayMentTransac extends AbstractBaseDomain{
	private String paymentId; 
	private Long payAmount;
	
}
