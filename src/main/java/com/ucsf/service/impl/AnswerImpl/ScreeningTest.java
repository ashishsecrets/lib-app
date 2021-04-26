package com.ucsf.service.impl.AnswerImpl;


import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.UserScreeningStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreeningTest {

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;
	
	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;
	
    public ScreenTestData screenTest(ScreeningAnswers lastAnswer){
        ScreenTestData screenTestData = new ScreenTestData();
    if (lastAnswer.getIndexValue()==1) {
        if (Integer.parseInt(lastAnswer.getAnswerDescription()) < 12 || Integer.parseInt(lastAnswer.getAnswerDescription()) > 65) {
            screenTestData.setIsFinished(true);
            screenTestData.setMessage("Sorry, this study is only for individuals between the ages of 12 and 65!");
        }
        else{
            screenTestData.setIsFinished(false);
            screenTestData.setMessage("continue");
        }
    }
     if (lastAnswer.getIndexValue()==2) {
        if (lastAnswer.getAnswerDescription()!= null && lastAnswer.getAnswerDescription().equals("No")) {
            screenTestData.setIsFinished(true);
            screenTestData.setMessage("Thank you for your interest! It looks like you do not qualify for this study, but if anything changes in the future please feel free to reach out. - UCSF Psoriasis and Skin Treatment Center");
           // screenTestData.setMessage("");
        }
        else{
            screenTestData.setIsFinished(false);
            screenTestData.setMessage("continue");
        }
    }
    
     if (lastAnswer.getIndexValue()==6) {
        if (lastAnswer.getAnswerDescription()!= null && lastAnswer.getAnswerDescription().equals("None of Above")) {
            screenTestData.setMessage("Thank you for your interest! It looks like you do not qualify for this study, but if anything changes in the future please feel free to reach out. - UCSF Psoriasis and Skin Treatment Center");
            screenTestData.setIsFinished(true);
        }
        else{
            screenTestData.setIsFinished(false);
            screenTestData.setMessage("continue");
        }
    }
        return screenTestData;
    }

}
