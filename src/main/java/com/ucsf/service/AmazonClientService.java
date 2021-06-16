package com.ucsf.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Service
public class AmazonClientService {

	@Autowired AmazonS3 s3client;
	
	@Value("${aws-bucketName}")
    private String bucketName;
	
	public Boolean awsPutObject(File file, String keyName) {
		try {
			s3client.putObject(bucketName, keyName, file);
			return true;
		} catch (AmazonServiceException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public S3Object awsGetObject(String keyName) {
		try {
			
			S3Object image = s3client.getObject(bucketName, keyName);
			
			//IOUtils.copy(image.getObjectContent(), new FileOutputStream(new File("/home/arshdeep/img2.png")));
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public Boolean awsCreateFolder(String folderName) {
		try {
			folderName = folderName+"/";
			if(!s3client.doesObjectExist(bucketName, folderName)) {
				InputStream input = new ByteArrayInputStream(new byte[0]);
		        ObjectMetadata metadata = new ObjectMetadata();
		        metadata.setContentLength(0);
		        s3client.putObject(new PutObjectRequest(bucketName, folderName, input, metadata));
			}
	        return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
