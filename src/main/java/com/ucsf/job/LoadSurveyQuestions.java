package com.ucsf.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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
import com.ucsf.model.SurveyAnswerChoice;
import com.ucsf.model.SurveyQuestion;
import com.ucsf.repository.SurveyChoiceRepository;
import com.ucsf.repository.SurveyQuestionRepository;

@EnableAutoConfiguration
@EnableScheduling
@Service
public class LoadSurveyQuestions {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	SurveyChoiceRepository surveyChoiceRepository;

	@Autowired
	SurveyQuestionRepository surveyRepository;

	@Value("${screening-questions-file}")
	private String filePath;

	// @Scheduled(cron="0 */1 * * * *")
	public void loadSheetContent() throws ClientProtocolException, IOException, GeneralSecurityException {
		// clear all previous data
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.update("TRUNCATE TABLE survey_questions");
		jdbcTemplate.update("TRUNCATE TABLE survey_ans_choice");
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
		String id = "1UhvTWTf_xp1NHm8VcVgFXxpxzAy8IOzWhWod1s_PqYU";

		List<String> sheetName = Arrays.asList("patient", "dermatology", "itch");
		for (String sheet : sheetName) {
			filePath = downloadSheetData(id, sheet);
			try {
				readDownloadedContentCsvData(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String downloadSheetData(String id, String sheetName) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String filePath = sheetName + ".csv";
		int gid = 0;
		try {
			if (filePath.contains("patient")) {
				gid = 923230846;
			}
			if (filePath.contains("dermatology")) {
				gid = 578148417;
			}
			if (filePath.contains("itch")) {
				gid = 902091073;
			}
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
			String questionDescription = null;
			String questionType = null;
			String choices = null;
			int counter = 0;
			while ((eczemaDataArray = reader.readNext()) != null) {
				if (counter > 0) {
					SurveyQuestion sc = new SurveyQuestion();
					String questionDescription2 = eczemaDataArray[0].replaceAll("\"", "");
					questionDescription = (questionDescription2 == null || questionDescription2.equals(""))
							? questionDescription
							: questionDescription2;
					String questionType2 = eczemaDataArray[1].replaceAll("\"", "");
					questionType = (questionType2 == null || questionType2.equals("")) ? questionType : questionType2;
					String choice2 = eczemaDataArray[2].replaceAll("\"", "");
					choices = (choice2 == null || choice2.equals("")) ? choices : choice2;
					sc.setDescription(questionDescription);
					sc.setEnabled(true);
					sc.setQuestionType(questionType);
					if (csvFile.contains("patient")) {
						sc.setSurveyId(1l);// repo
					}
					if (csvFile.contains("dermatology")) {
						sc.setSurveyId(2l);// repo
					}
					if (csvFile.contains("itch")) {
						sc.setSurveyId(3l);// repo
					}
					sc.setIndexValue(counter);
					surveyRepository.save(sc);
					if (choices != null && !choices.equals("")) {
						String[] split = choices.split("//");
						for (String c : split) {
							SurveyAnswerChoice choice = new SurveyAnswerChoice();
							choice.setChoice(c);
							choice.setQuestionId(sc.getId());
							surveyChoiceRepository.save(choice);
						}
					}
				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
