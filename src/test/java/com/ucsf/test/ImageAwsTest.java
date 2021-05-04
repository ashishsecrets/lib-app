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
public class ImageAwsTest {

    @Autowired AmazonS3 s3client;

    @Value("${aws-bucketName}")
    private String bucketName;

    @Test
    public void awsPutObject() {
        try {
            String file_path = "/Users/ashishsecrets/IdeaProjects/Backend/src/main/resources/ashish.jpg";
            String key_name = Paths.get(file_path).getFileName().toString();

            s3client.putObject(bucketName+"body_parts/special_areas", key_name, new File(file_path));

        } catch (AmazonServiceException e) {
            System.err.println("error : "+e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }

    @Test
    public void awsGetObject() {
        try {
            //String file_path = "/home/arshdeep/Pictures/cartwithbanner_2.png";
            String key_name = "ashish.jpg";//Paths.get(file_path).getFileName().toString();

            S3Object obj = s3client.getObject(bucketName, key_name);

            IOUtils.copy(obj.getObjectContent(), new FileOutputStream(new File("/Users/ashishsecrets/IdeaProjects/Backend/src/main/resources/ashish2.jpg")));

        } catch (Exception e) {
            System.err.println("error : "+e.getMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }

}
