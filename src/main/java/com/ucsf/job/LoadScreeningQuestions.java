package com.ucsf.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

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
public class LoadScreeningQuestions {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ChoiceRepository choiceRepository;

	@Autowired
	ScreeningQuestionRepository questionRepository;

	@Value("${screening-questions-file}")
	private String filePath;

	// @Scheduled(cron="0 */1 * * * *")
	public void loadSheetContent() throws ClientProtocolException, IOException, GeneralSecurityException {
		// clear all previous data
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.update("TRUNCATE TABLE questions");
		jdbcTemplate.update("TRUNCATE TABLE choices");
		jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
		String id = "1UhvTWTf_xp1NHm8VcVgFXxpxzAy8IOzWhWod1s_PqYU";

		filePath = downloadSheetData(id, "screening");
		try {
			readDownloadedContentCsvData(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String downloadSheetData(String id, String sheetName) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String filePath = sheetName+".csv";

		try {
			HttpGet request = new HttpGet(
					"https://docs.google.com/spreadsheets/d/"+id+"/gviz/tq?tqx=out:csv&sheet="+filePath);
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
					ScreeningQuestions sc = new ScreeningQuestions();
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
					// sc.setStudyId(1l);//repo
					sc.setIndexValue(counter);
					questionRepository.save(sc);
					if (choices != null && !choices.equals("")) {
						String[] split = choices.split("//");
						for (String c : split) {
							ScreeningAnsChoice choice = new ScreeningAnsChoice();
							choice.setChoice(c);
							choice.setQuestionId(sc.getId());
							choiceRepository.save(choice);
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
