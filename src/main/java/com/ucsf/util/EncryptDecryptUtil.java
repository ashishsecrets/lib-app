package com.ucsf.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


//import org.apache.tomcat.util.codec.binary.Base64;

public class EncryptDecryptUtil {


	//private static final String CONTENT = "thisneedstobestoredverysecurely";
	private static final String PASSPHRASE = "mysuperstrongpassword";
	private static final String PASSPHRASEMAC = "mysuperstrongpasswordMac";
	private static final int IV_LENGTH = 16;
	private static final int AES_KEY_LENGTH = 16;
	private static final int MAC_KEY_LENGTH = 16;
	private static final int MAC_LENGTH = 20;
	private static final int SALT_LENGTH = 16;
	private static final int SALT_MAC_LENGTH = 16;
	private static final int ITERATION_COUNT = 4096;
	private static final String AES = "AES";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String MAC_ALGORITHM = "HmacSHA1";


	Cipher cipher = null;
	byte[] iv = new byte[IV_LENGTH];
	byte[] secretBytes = null;
	byte[] secretBytesMac = null;

	
	public String encrypt(String plainText) throws Exception{
		cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
		SecureRandom sr = new SecureRandom();

		byte[] salt = new byte[SALT_LENGTH];
		sr.nextBytes(salt);

		SecretKey secretKey = factory.generateSecret(new PBEKeySpec(PASSPHRASE.toCharArray(), salt, ITERATION_COUNT, 256));
		secretBytes = secretKey.getEncoded();

		byte[] saltMac  = new byte[SALT_MAC_LENGTH];
		sr.nextBytes(saltMac);

		SecretKey secretKeyMac = factory.generateSecret(new PBEKeySpec(PASSPHRASEMAC.toCharArray(), saltMac, ITERATION_COUNT, 256));
		secretBytesMac = secretKeyMac.getEncoded(); 

		sr = new SecureRandom();
		sr.nextBytes(iv);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretBytes, 0, AES_KEY_LENGTH, AES), new IvParameterSpec(iv));

		byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
		byte[] result = concatArrays(iv, concatArrays(saltMac, (concatArrays(salt, encrypted))));
		byte[] macResult = getMAC(secretBytesMac, result);
		result = concatArrays(macResult, result);
		return Base64.getEncoder().encodeToString(result);
	}

	public String decrypt(String cipherText) throws Exception{
		byte[] result = Base64.getDecoder().decode(cipherText);
		byte[] macResult = new byte[MAC_LENGTH];
		byte[] salt = new byte[SALT_LENGTH];
		byte[] saltMac = new byte[SALT_MAC_LENGTH];
		cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		
		System.arraycopy(result, 0, macResult, 0, MAC_LENGTH);
		System.arraycopy(result, MAC_LENGTH, iv, 0, IV_LENGTH);
		System.arraycopy(result, MAC_LENGTH + IV_LENGTH, saltMac, 0, SALT_MAC_LENGTH);
		System.arraycopy(result, MAC_LENGTH + IV_LENGTH + SALT_MAC_LENGTH, salt, 0, SALT_LENGTH);

		byte[] encrypted = new byte[result.length-(MAC_LENGTH + IV_LENGTH + SALT_MAC_LENGTH + SALT_LENGTH)];
		System.arraycopy(result, MAC_LENGTH + IV_LENGTH + SALT_MAC_LENGTH + SALT_LENGTH, encrypted, 0, encrypted.length);
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
		
		SecretKey secretKeyMac = factory.generateSecret(new PBEKeySpec(PASSPHRASEMAC.toCharArray(), saltMac, ITERATION_COUNT, 256));
		secretBytesMac = secretKeyMac.getEncoded();
		
		if (!MessageDigest.isEqual(getMAC(secretBytesMac, concatArrays(iv, concatArrays(saltMac, (concatArrays(salt, encrypted))))), macResult)) {
			System.out.println("Invalid MAC");
		}
		
		SecretKey secretKey = factory.generateSecret(new PBEKeySpec(PASSPHRASE.toCharArray(), salt, ITERATION_COUNT, 256));
		secretBytes = secretKey.getEncoded();

		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretBytes, 0, AES_KEY_LENGTH, AES), new IvParameterSpec(iv));
		byte[] decrypted = cipher.doFinal(encrypted);
		return new String(decrypted, "UTF-8");
	}



	private static byte[] getDigest(byte[] mac) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		return digest.digest(mac);
	}

	private static byte[] getMAC(byte[] secretBytes, byte[] data) throws Exception {
		Mac mac = Mac.getInstance(MAC_ALGORITHM);
		mac.init(new SecretKeySpec(secretBytes, 0, MAC_KEY_LENGTH, MAC_ALGORITHM));
		return mac.doFinal(data);
	}

	private static byte[] concatArrays(byte[] first, byte[] second) {
		byte[] ret = new byte[first.length + second.length];
		System.arraycopy(first, 0, ret, 0, first.length);
		System.arraycopy(second, 0, ret, first.length, second.length);
		return ret;
	}


	//WORKING
	/*private final String key = "aesEncryptionKey";
	private final String initVector = "encryptionIntVec";

	public String encrypt(String value) {
	    try {
	        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

	    	byte[] encrypted = cipher.doFinal(value.getBytes());
	        return Base64.getEncoder().encodeToString(encrypted);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}


	public String decrypt(String encrypted) {
	    try {
	        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

	        return new String(original);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return null;
	}


	private String TOKEN ;
	private String salt;
	private int pwdIterations = 65536;
	private int keySize = 256;
	private byte[] ivBytes ;
	private String keyAlgorithm = "AES";
	private String encryptAlgorithm = "AES/CBC/PKCS5Padding";
	private String secretKeyFactoryAlgorithm = "PBKDF2WithHmacSHA1";

	public EncryptDecrypt(String password){
		TOKEN = password;
		this.salt = password.substring(0,16);
		this.ivBytes = password.substring(16,32).getBytes();
	}

	private String getSalt(){
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		String text = new String(bytes);
		return this.TOKEN.substring(0, 16);
	}


	public String encryptLatest(String plainText) throws Exception{
		byte[] saltBytes = salt.getBytes("UTF-8");

		SecretKeyFactory skf = SecretKeyFactory.getInstance(secretKeyFactoryAlgorithm);
		PBEKeySpec spec = new PBEKeySpec(TOKEN.toCharArray(), saltBytes, pwdIterations, keySize);
		SecretKey secretKey = skf.generateSecret(spec);
		SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);

		//AES initialization
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(ivBytes));

		//generate IV
		//this.ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));
		return new org.apache.tomcat.util.codec.binary.Base64().encodeAsString(encryptedText);
	}

	 *//**
	 * 
	 * @param encryptText
	 * @return decrypted text
	 * @throws Exception
	 *//*
	public String decryptLatest(String encryptText) throws Exception {
		byte[] saltBytes = salt.getBytes("UTF-8");
		byte[] encryptTextBytes = new org.apache.tomcat.util.codec.binary.Base64().decode(encryptText);

		SecretKeyFactory skf = SecretKeyFactory.getInstance(secretKeyFactoryAlgorithm);
		PBEKeySpec spec = new PBEKeySpec(TOKEN.toCharArray(), saltBytes, pwdIterations, keySize);
		SecretKey secretKey = skf.generateSecret(spec);
		SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);

		//decrypt the message
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

		byte[] decyrptTextBytes = null;
		try {
			decyrptTextBytes = cipher.doFinal(encryptTextBytes);
			return new String(decyrptTextBytes);
		} catch (IllegalBlockSizeException e) {
			// TODO: handle exception
			System.out.println("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			System.out.println("BadPaddingException");
		}
		String text = new String(decyrptTextBytes);
		return text;
	}


	String secret = "secret";

	public String generateHmacSHA256(String dataToGenerateHmacSHA) throws NoSuchAlgorithmException, InvalidKeyException {


    	Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(dataToGenerateHmacSHA.getBytes()));
        return hash;

	}*/

}