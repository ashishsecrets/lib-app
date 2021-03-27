package com.ucsf.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ucsf.model.Question;
import com.ucsf.model.Question.QuestionType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class questions {
	
	
	  // @Autowired 
	  //QuestionRepository  questionRepository;

	    @Test
	    public void addQuestions() throws Exception {
		 
		 Question ques = new Question();
		 
		 String [] choice = {"Male","Female"};
		 
		 ques.setChoice(choice.toString());
		 ques.setEnabled(true);
		 ques.setQuestionType(QuestionType.MULTIPLE_CHOICE);
		 ques.setDescription("Gender of the pateint");
		 ques.setCreatedBy("Gurpreet");
		 ques.setCreatedDate(new Date());
		 ques.setLastModifiedBy("Gurpreet");
	     ques.setLastModifiedDate(new Date());
	   //  questionRepository.save(ques);
	    }
	
}
