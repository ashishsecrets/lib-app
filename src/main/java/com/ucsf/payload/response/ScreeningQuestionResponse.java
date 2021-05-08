package com.ucsf.payload.response;

import java.util.List;

import com.ucsf.model.ScreeningAnsChoice;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;

import lombok.Data;

@Data
public class ScreeningQuestionResponse {

	//response sent to user in ans save api
	ScreeningQuestions screeningQuestions;
	ScreeningAnswers screeningAnswers;
	List<ScreeningAnsChoice> choices;
	Boolean isLastQuestion;
	String message;
	String information;
	Boolean isDisqualified;
	
}
