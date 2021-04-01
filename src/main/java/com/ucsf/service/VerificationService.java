package com.ucsf.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.authy.AuthyApiClient;
import com.ucsf.auth.model.User;

@Service
public class VerificationService {

	@Value("${twilio.twoFa}")
	private Boolean twoFa;

	@Value("${twilio.apiKey}")
	private String twilioAPIKey;

	private AuthyApiClient authyClient;

	private String authyAPIUrl = "https://api.authy.com/protected/json/phones/verification/start";

	private String authyAPICheckUrl = "https://api.authy.com/protected/json/phones/verification/check";

	public JSONObject sendVerificationCode(User user) {
		if (authyClient == null) {
			authyClient = new AuthyApiClient(twilioAPIKey);
		}
		JSONObject response = authyCall(user);
		return response;
	}

	public JSONObject authyCall(User user) {
		HttpsURLConnection con = null;
		StringBuffer response = null;
		JSONObject responseJSON = new JSONObject();
		try {
			URL obj = new URL(authyAPIUrl);
			con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("X-Authy-API-Key", twilioAPIKey);
			String urlParameters = "locale='en'&via=sms&phone_number=" + user.getPhoneNumber() + "&country_code="
					+ user.getPhoneCode() + "";

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
			JSONObject jsonObject = new JSONObject(response.toString());
			if (jsonObject.get("success") != null) {
				Boolean status = jsonObject.getBoolean("success");
				if (status) {
					responseJSON.put("isCheckToken", true);
					responseJSON.put("success", jsonObject.get("success"));
					responseJSON.put("phoneNumber", user.getPhoneNumber());
					responseJSON.put("countryCode", user.getPhoneCode());
					responseJSON.put("userId", user.getId());
				} else {
					responseJSON.put("success", jsonObject.get("success"));
				}
			}
		} catch (Exception e) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			response = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JSONObject jsonObject = new JSONObject(response.toString());
			responseJSON.put("success", false);
			responseJSON.put("message", jsonObject.get("message"));
			try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(response.toString());
		}
		return responseJSON;

	}

	public JSONObject otpCodeVerification(User user, String code) throws IOException {
		String url = authyAPICheckUrl + "?phone_number=" + user.getPhoneNumber() + "&country_code="
				+ user.getPhoneCode() + "&verification_code=" + code + "";
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