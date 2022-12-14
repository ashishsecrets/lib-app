package com.ucsf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

	public static Long getAge(String birthDate) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDate localDate = LocalDate.parse(birthDate, formatter);

			LocalDate now = LocalDate.now();
			long years = ChronoUnit.YEARS.between(localDate, now);
			return years;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean validateDOB(String strDate) {
		if (strDate.trim().equals("")) {
			return true;
		} else {
			SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
			sdfrmt.setLenient(false);
			try {
				Date javaDate = sdfrmt.parse(strDate);
				System.out.println(strDate + " is valid date format");
			} catch (ParseException e) {
				System.out.println(strDate + " is Invalid Date format");
				return false;
			}
			return true;
		}
	}
}
