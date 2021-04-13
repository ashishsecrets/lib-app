package com.ucsf.payload.response;

import java.util.List;

import com.ucsf.model.Choices;
import com.ucsf.model.ScreeningQuestions;

import lombok.Data;

@Data
public class ScreeningQuestionResponse {

	ScreeningQuestions screeningQuestions;
	List<Choices> choices;
	
}