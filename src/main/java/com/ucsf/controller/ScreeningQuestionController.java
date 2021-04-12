package com.ucsf.controller;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.model.Choices;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.repository.ChoiceRepository;
import com.ucsf.repository.ScreeningQuestionRepository;


@RestController
@CrossOrigin
@RequestMapping("/api/questions")
public class ScreeningQuestionController {
	
	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;
	
	@Autowired
	ChoiceRepository choiceRepository;

	@RequestMapping(value = "/question/{studyId}/{indexValue}", method = RequestMethod.GET)
	public ResponseEntity<?> createAuthenticationToken(@PathVariable Long studyId,@PathVariable int indexValue) throws Exception {
		JSONObject jsonObject = new JSONObject();
		ScreeningQuestions sc = screeningQuestionRepository.findByStudyIdAndIndexValue(studyId, indexValue);
		/*
		 * List<Choices> choice = choiceRepository.findByQuestionId(sc.getId());
		 * jsonObject.put("Question", sc); JSONArray array = new JSONArray();
		 * for(Choices c :choice) { array.put(c); } jsonObject.put("Choice", array);
		 */
		return new ResponseEntity(sc, HttpStatus.OK);	
		}
}