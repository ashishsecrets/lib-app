package com.ucsf.service.impl.AnswerImpl;


import com.ucsf.model.ScreeningAnswers;
import org.springframework.stereotype.Service;

@Service
public class ScreeningTest {

    public ScreenTestData screenTest(ScreeningAnswers lastAnswer){
        ScreenTestData screenTestData = new ScreenTestData();

        if(Integer.parseInt(lastAnswer.getAnswerDescription()) < 12 || Integer.parseInt(lastAnswer.getAnswerDescription()) > 65){
           screenTestData.setIsFinished(true);
           screenTestData.setMessage("Sorry, this study is only for individuals between the ages of 12 and 65!");
        }
        else{
            screenTestData.setIsFinished(false);
            screenTestData.setMessage("continue");
        }

        return screenTestData;
    }


}
