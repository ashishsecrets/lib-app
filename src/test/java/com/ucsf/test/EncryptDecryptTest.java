package com.ucsf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ucsf.UcsfMainApplication;
import com.ucsf.util.EncryptDecryptUtil;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcsfMainApplication.class)
public class EncryptDecryptTest {

    EncryptDecryptUtil encryptDecrypt = new EncryptDecryptUtil();

	@Test
	public void encrypt() throws Exception {
		
		JSONObject jsonObject2 = new JSONObject();
		/*jsonObject2.put("Email address", "gurpreet.kaur@redblink.net");
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
		jsonObject2.put("Flareup Intensity", "Medium");*/
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

	@Test
	public void decrypt() throws Exception {
		String encryptedText = "C/fHJf9V0i5t0AwCZUGaYWmPYQYjjesnfHPzVW+pdGWvZEquB7u4FzBgEIx8+ViRiyjSw+J2h/pCg394S5asWbygWuZ1GMIN539NySk/X/uNqyFYkMLS5Z9OvbFfLaxQ1PzfaokWwR257V9VQDrw6MFle9qyEXYT5QjSeBHDBaDOPEOYx7cFAAb6vVlRQr7cgxAwWvntGGw1uYCYwECzHrxl95/i3YaXBW+re3Y712v3lTGaa7PP4XoM6h1bmxKv59YPPMqsyXAWoluRCjcNz9gB3dvCbtsjfoA6QjdMYzE1L7K189Zk39YVNuq0Ux+CAUZysyWXFOgFGQLS+pYCBfZh992YJVJqrQXH/pc8DhU1h9B1O1pynbTRM6e4cfAnr34hEyvdlYDdxiNI8AcYdWkMYLGxcRKPNNyKl9J5dPOAlhRCXTVa7LxkRtzTYVgJcTGHNO9iTHbF+NgfE3SgiYfKVoKz2UOhl9enBuKHIfaa4rQTJ0tIKlWK3KWcTRAntHcT9A==";
		String decryptedFlareupData = encryptDecrypt.decrypt(encryptedText);
		System.out.println(decryptedFlareupData);
	}

	@Test
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

	}
}
