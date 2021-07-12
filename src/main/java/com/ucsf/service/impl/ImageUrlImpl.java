package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.ConsentForms;
import com.ucsf.model.StudyImages;
import com.ucsf.model.UserConsent;
import com.ucsf.payload.request.AffectedPartRequest;
import com.ucsf.payload.request.BodyPartRequest;
import com.ucsf.payload.response.StudyBodyPartsResponse;
import com.ucsf.repository.ImageRepository;
import com.ucsf.service.AmazonClientService;
import com.ucsf.service.ImageUrlService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service("ImageUrlService")
public class ImageUrlImpl implements ImageUrlService {

    @Autowired
    ImageRepository imageRepository;

    @Override
    public List<StudyImages> getImageUrls(Long studyId, Long userId) {

        List<StudyImages> list = null;

        if(studyId != null && userId != null){

            list = imageRepository.findByStudyIdAndUserId(studyId, userId);

        }

        return list;
    }

    @Override
    public String saveFile(File file, String fileFolder) {
        try {

            String path = "./src/main/resources/images/";


            String filePath = fileFolder+"/"+file.getName();
            Files.createDirectories(Paths.get(path + fileFolder));
            file.renameTo(new File(path + filePath));
            //amazonClientService.awsPutObject(file, filePath);

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveImage(User user, BodyPartRequest request) {

        try {

            String fileName = user.getId()+"_"+new Date().getTime();
            File bodyPartFile = decodeBase64String(request.getPartImage(), fileName+".jpeg");

            StudyImages studyImages = imageRepository.findById(request.getId()).get();

            saveFile(bodyPartFile, studyImages.getImageUrl());
            int count = studyImages.getCount();
            studyImages.setCount(count+1);
            List<String> fileNameList = studyImages.getFilename();
            fileNameList.add(fileName+".jpeg");
            studyImages.setFilename(fileNameList);
            List<String> fileDescriptionList = studyImages.getFileDescription();
            fileDescriptionList.add(request.getDescription());
            //Parameter Description is Obslete - comenting out.

            imageRepository.save(studyImages);

            bodyPartFile.delete();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public InputStream getImage(String imagePath) throws IOException {

        String path = "./src/main/resources/images/";

        File file = ResourceUtils.getFile(path + imagePath);

        return file.toURL().openStream();
    }

    @Override
    public void saveAffectedImage(User user, AffectedPartRequest request) {


        try {

            String fileName = user.getId()+"_"+new Date().getTime();
            File affectedAreaFile = decodeBase64String(request.getPartImage(), fileName+".jpeg");

            List<StudyImages> studyImagesList = imageRepository.findByUserIdAndImageType(user.getId(), request.getImageType());

            for(StudyImages studyImages : studyImagesList){

            saveFile(affectedAreaFile, studyImages.getImageUrl());
            int count = studyImages.getCount();
            studyImages.setCount(count+1);
            List<String> fileNameList = studyImages.getFilename();
            fileNameList.add(fileName+".jpeg");
            studyImages.setFilename(fileNameList);
            List<String> fileDescriptionList = studyImages.getFileDescription();
            fileDescriptionList.add(request.getDescription());
            //Parameter Description is Obslete - comenting out.

            imageRepository.save(studyImages);

            affectedAreaFile.delete();

            }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    private File decodeBase64String(String signatureImageUrl, String fileName) {
        File file = new File(fileName);
        byte[] decodedBytes = Base64.decodeBase64(signatureImageUrl);
        try {
            FileUtils.writeByteArrayToFile(file, decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
