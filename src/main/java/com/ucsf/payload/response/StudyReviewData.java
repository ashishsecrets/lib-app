package com.ucsf.payload.response;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import lombok.Data;
import java.util.List;

@Data
public class StudyReviewData {

    private ScreeningQuestions question;
    private ScreeningAnswers answer;

    public StudyReviewData(ScreeningQuestions question, ScreeningAnswers answer) {
        this.question = question;
        this.answer = answer;
    }
}
