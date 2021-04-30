package com.ucsf.util;

import java.util.Random;

public class AppUtil {
	public static String generateOtpCode(int len) {
		// Using numeric values
		String numbers = "0123456789";

		// Using random method
		Random rndm_method = new Random();
		char[] otp = new char[len];
		for (int i = 0; i < len; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return String.valueOf(otp);
	}
}
