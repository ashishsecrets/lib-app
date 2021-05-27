package com.ucsf.job;

import com.opencsv.CSVReader;
import com.ucsf.auth.model.User;
import com.ucsf.model.UcsfSurvey;
import com.ucsf.model.UserTasks;
import com.ucsf.repository.SurveyRepository;
import com.ucsf.repository.TasksRepository;
import com.ucsf.repository.UserRepository;
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

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@EnableAutoConfiguration
@EnableScheduling
@Service
public class LoadStudyTasks {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TasksRepository tasksRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${screening-questions-file}")
    private String filePath;

    // @Scheduled(cron="0 */1 * * * *")
    public void loadSheetContent() throws ClientProtocolException, IOException, GeneralSecurityException {
        // clear all previous data
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.update("TRUNCATE TABLE user_tasks");
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
        String id = "1tf9ftfhTOE0vO8BV4tms2oTAO24l9tNZnM072KDH2TI";

        //google sheet link for tasks
        //https://docs.google.com/spreadsheets/d/1tf9ftfhTOE0vO8BV4tms2oTAO24l9tNZnM072KDH2TI/edit#gid=0

        filePath = downloadSheetData(id, "tasks");
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

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] eczemaDataArray;
            String title = null;
            String description = null;
            String taskType = null;
            String startDate = null;
            String endDate = null;
            String userId = null;
            String studyId = null;

            int counter = 0;
            while ((eczemaDataArray = reader.readNext()) != null) {
                if (counter > 0) {

                    String title2 = eczemaDataArray[0].replaceAll("\"", "");
                    title = (title2 == null || title2.equals("")) ? title : title2;
                    String description2 = eczemaDataArray[1].replaceAll("\"", "");
                    description = (description2 == null || description2.equals("")) ? description : description2;
                    String taskType2 = eczemaDataArray[2].replaceAll("\"", "");
                    taskType = (taskType2 == null || taskType2.equals("")) ? taskType : taskType2;
                    String startDate2 = eczemaDataArray[3].replaceAll("\"", "");
                    startDate = (startDate2 == null || startDate2.equals("")) ? startDate : startDate2;
                    String endDate2 = eczemaDataArray[4].replaceAll("\"", "");
                    endDate = (endDate2 == null || endDate2.equals("")) ? endDate : endDate2;
                    String userId2 = eczemaDataArray[5].replaceAll("\"", "");
                    userId = (userId2 == null || userId2.equals("")) ? userId : userId2;
                    String studyId2 = eczemaDataArray[6].replaceAll("\"", "");
                    studyId = (studyId2 == null || studyId2.equals("")) ? studyId : studyId2;

                    if(userId.equals("all")) {
                        Iterable<User> users = userRepository.findAll();
                        Long id;

                        for (User user : users) {
                            id = user.getId();

                            UserTasks task = new UserTasks();
                            task.setTitle(title);
                            task.setDescription(description);
                            task.setTaskType(taskType);
                            task.setStartDate(formatter.parse(startDate));
                            task.setEndDate(formatter.parse(endDate));

                            //users.forEach(System.out::println);
                            task.setUserId(id);
                            assert studyId != null;
                            task.setStudyId(Long.parseLong(studyId));

                            if (taskType.equals("survey")) {

                                UcsfSurvey survey = surveyRepository.findByTitle(title);
                                task.setTaskId(survey.getId());

                            } else {
                                task.setTaskId((long) counter+100);
                                task.setProgress(0);
                            }

                            tasksRepository.save(task);

                        }
                    }

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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
