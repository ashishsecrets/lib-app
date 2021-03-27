package com.ucsf.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;

import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucsf.auth.model.User;

public class AppUtil {
	
	static String ROLE_PREFIX = "ROLE_";
	
	public static SimpleDateFormat dateFormatMMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
	
	public static SimpleDateFormat dateFormatYYYYMMDDHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("unchecked")
	public static String returnJson(String status, Boolean isError, Map<String, Object>... obj) {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		map.put("success", true);
		map.put("isCheckToken", false);
		for (Map<String, Object> a : obj) {
			map.putAll(a);
		}
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return "{status : \"parsing Error " + e.getMessage() + "\"}";
		}
	}


	public static List<String> determineTargetRole(UserDetails user) {
		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority a : user.getAuthorities()) {
			roles.add(a.getAuthority());
		}
		return roles;
	}
	

	public static JSONObject sendVerificationCode(User user, String twilioAPIKey) throws IOException{
		HttpsURLConnection con = null;
		StringBuffer response = null;
		JSONObject responseJSON = new JSONObject();
		String url = "https://api.authy.com/protected/json/phones/verification/start";
		try{
		URL obj = new URL(url);
		con = (HttpsURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("X-Authy-API-Key", twilioAPIKey);
	//	String urlParameters = "locale='en'&via=sms&phone_number="+user.getPhoneNumber()+"&country_code="+user.getCountryCode()+"";
		String urlParameters = "locale='en'&via=sms&phone_number="+"8558940243"+"&country_code="+"IN"+"";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
	    response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("Twilio sms details : "+ response.toString());
		JSONObject jsonObject = new JSONObject(response.toString());
		
		if(jsonObject.get("success") != null){
			Boolean status = jsonObject.getBoolean("success");
			if(status){
				responseJSON.put("isCheckToken", true);
				responseJSON.put("success", jsonObject.get("success"));
				//responseJSON.put("phoneNumber", user.getPhoneNumber());
				//responseJSON.put("countryCode", user.getCountryCode());
				responseJSON.put("userId", user.getId());
			} else {
				responseJSON.put("success", jsonObject.get("success"));
			}
		}
		}catch (Exception e) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			 response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			JSONObject jsonObject = new JSONObject(response.toString());
			responseJSON.put("success", false);
			responseJSON.put("message", jsonObject.get("message"));
			in.close();
			System.out.println(response.toString());
		}
		return responseJSON;
	}
	
	public static JSONObject verificationCode(String phoneNumber, String countryCode, String code, String twilioAPIKey) throws IOException{
		String url = "https://api.authy.com/protected/json/phones/verification/check?phone_number="+phoneNumber+"&country_code="+countryCode+"&verification_code="+code+"";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("X-Authy-API-Key", twilioAPIKey);
		StringBuffer response = new StringBuffer();
		int responseCode = con.getResponseCode();
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return new JSONObject(response.toString());
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return new JSONObject(response.toString());
		}
		
	}
	
}
