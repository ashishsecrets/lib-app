package com.ucsf.test;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.ucsf.UcsfMainApplication;
import com.ucsf.util.EncryptDecryptUtil;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcsfMainApplication.class)
public class EncryptDecryptTest {

	EncryptDecryptUtil encryptDecrypt = new EncryptDecryptUtil();

	/*@Test
	public void encrypt() throws Exception {

		JSONObject jsonObject2 = new JSONObject();
		*//*jsonObject2.put("Email address", "gurpreet.kaur@redblink.net");
		jsonObject2.put("Zip Code", "160022");
		jsonObject2.put("Hi Temp", "90.0");
		jsonObject2.put("Lo Temp", "78.0");
		jsonObject2.put("Max Humidity", "");
		jsonObject2.put("Min Humidity", "");
		jsonObject2.put("Current Temp", "90.0");
		jsonObject2.put("Pollen Intensity", "");
		jsonObject2.put("Current Humidity", "80");
		jsonObject2.put("Data Entry Date", "2019-08-29T00:00:00");
		jsonObject2.put("Pollen Counts", "");
		jsonObject2.put("Flareup Intensity", "Medium");*//*
		String jsonObject2String = "{\n" +
				"    \"data\": [\n" +
				"        {\n" +
				"            \"userImageCount\": 2,\n" +
				"            \"study\": {\n" +
				"                \"default\": false,\n" +
				"                \"description\": \"This study will track your eczema status and lifestyle measurements for one year. Five in-person research visits and in-app questionnaires and pictures will be required.\",\n" +
				"                \"id\": 1,\n" +
				"                \"title\": \"Eczema Tracking Study\",\n" +
				"                \"enabled\": false,\n" +
				"                \"studyStatus\": \"5\"\n" +
				"            }\n" +
				"        }\n" +
				"    ]\n" +
				"}";
		jsonObject2 = new JSONObject(jsonObject2String);
		String encryptedFlareupData = encryptDecrypt.encrypt(jsonObject2.toString());
		System.out.println(encryptedFlareupData);
	}
*/
	@Test
	public void decrypt() throws Exception {
		String result = "0e743c71a76887dc65ad896955f27ab10f262c2a4fbda67e336fdb8d2a5d0d4ba670b43d3749eaaaf5cb6f3cfd2ec90ec3d522ac988554a876ad7b74449b220ba153132f9dc6447aMgzo44lzrKsF8Uh65mUmvQ5ece3a078444cf24f9a4ad2e3fcda6f2";
		String resultdata = encryptDecrypt.decrypt(result);
		System.out.println(resultdata);
		//String rest2 = "OTEwY2M0MzU2ODFlZjA3M2FjZWE1M2E5MzYzYjI2YzU5NDU0ZGM0ZGFjMGZkODM3MzkwMTczY2I4MzJjODJkNDQ1NDEwYzIxZDAwMTEyYTBlOWJmNjMzYzg2YjRmZTA4OTcyMWY0YWZVYUdpYzZyRWFsZ2x3VnhQcEd5Z3d3OWQ0Y2VhMmFlMWNlZGRjN2VlYTRlMTNmZGVmODlhMWM=";
		//String rest2data = encryptDecrypt.decrypt(rest2);
		//System.out.println(rest2data);
		//String rest = "ccb7617f18d86e9559f76ff773a18b9bWPE9UBJROVCsTVy7OfTaHgef7d6876f81b6aa75856c2f4f30899c4";
		//String restdata = encryptDecrypt.decrypt(rest);
		//System.out.println(restdata);

	}

	/*@Test
	public void hmacSHA256AndEncrypt() throws Exception {

		String secret = "secret";
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("email", "gurpreet.kaur@redblink.net");
		jsonObject2.put("zipCode", "160022");
		jsonObject2.put("hiTemp", "90.0");
		jsonObject2.put("loTemp", "78.0");
		jsonObject2.put("maxHumidity", "");
		jsonObject2.put("minHumidity", "");
		jsonObject2.put("currentTemp", "90.0");
		jsonObject2.put("pollenIntensity", "");
		jsonObject2.put("currentHumidity", "80");
		jsonObject2.put("dataEntryDate", "2019-08-30T00:00:00");
		jsonObject2.put("pollenCounts", "");
		jsonObject2.put("flareupIntensity", "Medium");

		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(jsonObject2.toString().getBytes()));
		System.out.println("hash : " + hash);

		String encryptedFlareupData = encryptDecrypt.encrypt(jsonObject2.toString());
		System.out.println("encryptedFlareupData using encrypt :" + encryptedFlareupData);

	}*/
}
