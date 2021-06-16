package com.ucsf.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

import com.ucsf.model.UcsfSurvey;
import com.ucsf.repository.SurveyRepository;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
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
	SurveyQuestionRepository surveyQuestionRepository;

	@Autowired
	SurveyRepository surveyRepository;

	//@Value("${survey-patient-questions-file}")
	private String filePath;

	//@Value("${survey-dermatology-questions-file}")
	private String filePath2;

	//@Value("${survey-itch-questions-file}")
	private String filePath3;

	private class Sheet {
		String id;
		String sheetName;
		public Sheet(String id, String sheetName) {
			this.id = id;
			this.sheetName = sheetName;
		}
	}

	// @Scheduled(cron="0 */1 * * * *")
	public void loadSheetContent() throws ClientProtocolException, IOException, GeneralSecurityException {
		// clear all previous data
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.update("TRUNCATE TABLE survey_questions");
		jdbcTemplate.update("TRUNCATE TABLE survey_ans_choice");
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");

		Map<String, Sheet> sheetName = new HashMap();
		List<Sheet> list = new ArrayList<>();
		list.add(new Sheet("12Q-7nq9fhUM8BeEBWrn8hy26eLdDjx9g3R_ZeixjRVw", "POEM"));
		list.add(new Sheet("1MK20TCV04yCB_md1JV-OL0QyZkkXI6b8LrO4zvHW8dk", "DLQI"));
		list.add(new Sheet("1DoSxuwLDnqzYHIzsCbi85I1KMS8yfmjv4s1vJRbUtnw", "NRS_itch"));
		sheetName.put("patient", list.get(0));
		sheetName.put("dermatology", list.get(1));
		sheetName.put("itch", list.get(2));
		for(Map.Entry<String, Sheet> sheet : sheetName.entrySet()) {
			filePath = downloadSheetData(sheet.getValue().id, sheet.getValue().sheetName);
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
			if (filePath.contains("POEM")) {
				gid = 923230846;
			}
			if (filePath.contains("DLQI")) {
				gid = 578148417;
			}
			if (filePath.contains("NRS_itch")) {
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
					if (csvFile.contains("POEM")) {
						sc.setSurveyId(surveyRepository.findByTitle("POEM").getId());// repo
					}
					if (csvFile.contains("DLQI")) {
						sc.setSurveyId(surveyRepository.findByTitle("DLQI").getId());// repo
					}
					if (csvFile.contains("NRS_itch")) {
						sc.setSurveyId(surveyRepository.findByTitle("NRS itch").getId());// repo
					}
					sc.setIndexValue(counter);
					surveyQuestionRepository.save(sc);
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
