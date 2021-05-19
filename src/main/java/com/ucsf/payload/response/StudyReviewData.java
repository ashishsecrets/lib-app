package com.ucsf.payload.response;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.StudyImages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudyReviewData {

    private String question;
    private String answer;
    private List<StudyImages> studyImages = new ArrayList<StudyImages>();

    public StudyReviewData(String question, String answer, List<StudyImages> studyImages) {
        this.question = question;
        this.answer = answer;
        this.studyImages = studyImages;
    }
}
