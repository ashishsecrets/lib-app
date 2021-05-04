package com.ucsf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.ucsf.UcsfMainApplication;
import com.ucsf.payload.request.Note;
import com.ucsf.service.PushNotificationService;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcsfMainApplication.class)
public class TestClass {

	@Autowired AmazonS3 s3client;
	@Autowired PushNotificationService pushNotificationService;
	
	@Value("${aws-bucketName}")
    private String bucketName;
	
	@Test
	public void awsPutObject() {
		try {
			String file_path = "/home/rbpcadmin/Pictures/h.jpeg";
			String key_name = Paths.get(file_path).getFileName().toString();

			s3client.putObject(bucketName, key_name, new File(file_path));
			
		} catch (AmazonServiceException e) {
			System.err.println("error : "+e.getErrorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}
	
	@Test
	public void awsGetObject() {
		try {
			String file_path = "/home/arshdeep/Pictures/cartwithbanner_2.png";
			String key_name = "cartwithbanner.png";//Paths.get(file_path).getFileName().toString();

			S3Object obj = s3client.getObject(bucketName, key_name);
			
			IOUtils.copy(obj.getObjectContent(), new FileOutputStream(new File("/home/arshdeep/Pictures/cartwithbanner_3.png")));
			
		} catch (Exception e) {
			System.err.println("error : "+e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}
	
	@Test
	public void sendPushNotification() {
		try {
			Note note = new Note();
			note.setContent("This is to test push notification");
			note.setSubject("UCSF");
			
			Map<String, String> data = new TreeMap<String, String>();
			data.put("1", "value");
			data.put("2", "value2");
			
			note.setData(data);
			
			String msgId = pushNotificationService.sendNotification(note, "dJIVnumt0kNSgqyp1CpItf:APA91bHQMjIQAD237ZdZvxAfKhdZ5i5axDmOoFQehWwPg3Qr4lTvMMZjAHWAYlqmtPovW04UM8rUjbDW3IZsUqOjIs-KYKVSA8us44RflHb4ZyB_7sK_Oo9HTqcIu2IAIGz_dWXR_7_T");
			System.out.println("msgId : " + msgId);//msgId : projects/skin-tracker-3815b/messages/1620026465079461
		} catch (Exception e) {
			System.err.println("error : "+e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}
}
