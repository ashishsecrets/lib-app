package com.ucsf.service.impl;


import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.response.StudyInfoData;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.UserScreeningStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyInfoCheck {
	
    public StudyInfoData screenTest(ScreeningAnswers lastAnswer, int quesIncrement, Long studyId) {
        StudyInfoData screenTestData = new StudyInfoData();

        if(studyId == 1){

        if (lastAnswer.getIndexValue() == 1) {
            if (!lastAnswer.getAnswerDescription().equals("")  && (Integer.parseInt(lastAnswer.getAnswerDescription()) < 12 || Integer.parseInt(lastAnswer.getAnswerDescription()) > 65)) {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.TRUE);
                screenTestData.setMessage("Sorry, this study is only for individuals between the ages of 12 and 65!");
            } else {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.NONE);
            }
        }
        if (lastAnswer.getIndexValue() == 2) {
            if (lastAnswer.getAnswerDescription() != null && lastAnswer.getAnswerDescription().equals("No")) {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.TRUE);
                screenTestData.setMessage("Thank you for your interest! It looks like you do not qualify for this study, but if anything changes in the future please feel free to reach out. - UCSF Psoriasis and Skin Treatment Center");
                // screenTestData.setMessage("");
            } else {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.NONE);

            }
        }

        if (lastAnswer.getIndexValue() == 3) {
            if (lastAnswer.getAnswerDescription() != null && !lastAnswer.getAnswerDescription().equals("Primary care doctor")) {
                screenTestData.setMessage("");
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.FALSE);
            } else {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.NONE);

            }
        }

        //check if user goes backwards

        if (lastAnswer.getIndexValue() == 5 && quesIncrement == -1) {
            if (lastAnswer.getAnswerDescription() != null && !lastAnswer.getAnswerDescription().equals("Primary care doctor")) {
                screenTestData.setMessage("");
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.FALSE);
            } else {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.NONE);

            }
        }

        if (lastAnswer.getIndexValue() == 6) {
            if (lastAnswer.getAnswerDescription() != null && !lastAnswer.getAnswerDescription().equals("None of Above")) {
                screenTestData.setMessage("Thank you for your interest! It looks like you do not qualify for this study, but if anything changes in the future please feel free to reach out. - UCSF Psoriasis and Skin Treatment Center");
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.TRUE);
            } else {
                screenTestData.setIsFinished(StudyInfoData.StudyInfoSatus.NONE);
            }
        }

    }
        return screenTestData;
    }

}