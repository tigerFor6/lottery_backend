package com.linglong.lottery_backend.user.account.util;

import java.util.Random;
import java.util.UUID;

/**
 * Description
 *
 * @author yixun.xing
 * @since 19 三月 2019
 */
public class Captcha {

	public static String genCaptachaCode(){
		Random rnd = new Random();
		int n = 100000 + rnd.nextInt(900000);
		return String.valueOf(n);
	}

	public static String genUserId(String requestIp,String mobileNumber){
		long millis = System.currentTimeMillis();
		long threadId = Thread.currentThread().getId();

		String constructStr=millis+"-"+System.nanoTime()+"-"+threadId+"-"+String.join("", requestIp.split(":"))+"-"+mobileNumber;
		UUID userId=UUID.fromString(constructStr);
		return userId.toString();
	}
}
