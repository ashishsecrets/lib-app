package com.ucsf.payload.response;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import lombok.Data;
import java.util.List;

@Data
public class StudyReviewData {

    private String question;
    private String answer;

    public StudyReviewData(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
