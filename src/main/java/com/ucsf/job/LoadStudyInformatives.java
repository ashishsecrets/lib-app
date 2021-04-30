package com.ucsf.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import com.ucsf.model.StudyInformative;
import com.ucsf.repository.InformativeRepository;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.ucsf.model.ScreeningAnsChoice;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.repository.ChoiceRepository;
import com.ucsf.repository.ScreeningQuestionRepository;

@EnableAutoConfiguration
@EnableScheduling
@Service
public class LoadStudyInformatives {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    InformativeRepository informativeRepository;

    @Value("${screening-questions-file}")
    private String filePath;

    // @Scheduled(cron="0 */1 * * * *")
    public void loadSheetContent() throws ClientProtocolException, IOException, GeneralSecurityException {
        // clear all previous data
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.update("TRUNCATE TABLE study_information");
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
        String id = "1TujH7L0WsnZvq7md-nE78vffB7TwaeeBsKHMNIZJUz4";

        //https://docs.google.com/spreadsheets/d/1TujH7L0WsnZvq7md-nE78vffB7TwaeeBsKHMNIZJUz4/edit#gid=0

        filePath = downloadSheetData(id, "informatives");
        try {
            readDownloadedContentCsvData(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String downloadSheetData(String id, String sheetName) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String filePath = sheetName+".csv";
        int gid = 0;

        try {
            HttpGet request = new HttpGet("https://docs.google.com/spreadsheets/d/" + id + "/gviz/tq?tqx=out:csv&sheet="
                    + filePath + "&gid=" + gid);
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                if (entity != null) {

                    FileOutputStream fos = new FileOutputStream(new File(filePath));
                    int inByte;
                    while ((inByte = is.read()) != -1)
                        fos.write(inByte);
                    is.close();
                    fos.close();
                }

            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return filePath;
    }

    public void readDownloadedContentCsvData(String csvFile) {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] eczemaDataArray;
            String informationDescription = null;
            String indexValue = null;
            String studyId = null;
            int counter = 0;
            while ((eczemaDataArray = reader.readNext()) != null) {
                if (counter > 0) {
                    StudyInformative sc = new StudyInformative();
                    String informationDescription2 = eczemaDataArray[0].replaceAll("\"", "");
                    informationDescription = (informationDescription2 == null || informationDescription2.equals(""))
                            ? informationDescription
                            : informationDescription2;
                    String indexValue2 = eczemaDataArray[1].replaceAll("\"", "");
                    indexValue = (indexValue2 == null || indexValue2.equals("")) ? indexValue : indexValue2;
                    String studyId2 = eczemaDataArray[2].replaceAll("\"", "");
                    studyId = (studyId2 == null || studyId2.equals("")) ? studyId : studyId2;
                    sc.setInfoDescription(informationDescription);
                    sc.setIndexValue(Integer.parseInt(indexValue));
                    sc.setStudyId(Long.parseLong(studyId));//repo
                    //sc.setIndexValue(questionRepository.findFirstByStudyIdOrderByIndexValueDesc(1l).getIndexValue());
                    //sc.setIndexValue(counter);
                    informativeRepository.save(sc);
                    /*if (questionId != null && !questionId.equals("")) {
                        String[] split = questionId.split("//");
                        for (String c : split) {
                            ScreeningAnsChoice choice = new ScreeningAnsChoice();
                            choice.setChoice(c);
                            choice.setQuestionId(sc.getId());
                            choiceRepository.save(choice);
                        }
                    }*/
                }
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
