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
import org.junit.runner.RunWith;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ucsf.UcsfMainApplication;
import com.ucsf.util.EncryptDecryptUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcsfMainApplication.class)
public class EncryptDecryptTest {

    EncryptDecryptUtil encryptDecrypt = new EncryptDecryptUtil();

	@Test
	public void encrypt() throws Exception {
		
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("Email address", "gurpreet.kaur@redblink.net");
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
		jsonObject2.put("Flareup Intensity", "Medium");
		String encryptedFlareupData = encryptDecrypt.encrypt(jsonObject2.toString());
		System.out.println(encryptedFlareupData);
	}

	@Test
	public void decrypt() throws Exception {
		String encryptedText = "W1KzIVpdt0G2BphYHfZRp0OzdQtB6pHIjrpnFgT+4zAMdbtAFJoKGdSCMNd1AwR17qVmk85zn8wJsu8N8PmNjmpbZPtnhnK6FFg6FUMTcfwI5iq0Bbu9fsJagtFOxe+ymwzvg5bXBT9GT6hh2+YNMnUHWJbpck0jRgxE9Yug79yTyFxRSg7W7NsuQaXFi/3BD8kMluCq/CBhWkY/QnSyLeHx/GXK7AkL+CpZM5f8p7Netw7SPx0oS0HKyQlf5UJIRGdzGwu7KPhg3DtMBMX1LE+zj6OIjfz/u3J7632cBOAvnOZnHhiW9yQ9FlRr+THy+G4O7jk1z/ELYvbtKuxL9RZ8oO+zXPAXkjW9QaU0MRa4+NuFq4Pzxwz8guJFX4udva6PLtzhNHpGdDjrXe5y5Smt2/lNAC/opka3Vzo8pDVJLGvQofycKkBhUp+bN220nVFSwtMXkUkp49yQf2AqgvdDhsxMyPMGyzoEFWbSBUl7DFpH";
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
