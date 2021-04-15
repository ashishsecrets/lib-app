package com.ucsf.test;

import java.util.Date;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ucsf.model.ScreeningQuestions;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class questions {
	
	    @Test
	    public void addQuestions() throws Exception {
		 
		 ScreeningQuestions ques = new ScreeningQuestions();
		 
		 String [] choice = {"Male","Female"};
		 
		 ques.setEnabled(true);
		// ques.setQuestionType(QuestionType.MULTIPLE_CHOICE);
		 ques.setDescription("Gender of the pateint");
		 ques.setCreatedBy("Gurpreet");
		 ques.setCreatedDate(new Date());
		 ques.setLastModifiedBy("Gurpreet");
	     ques.setLastModifiedDate(new Date());
	   //  questionRepository.save(ques);
	    }
	
}
