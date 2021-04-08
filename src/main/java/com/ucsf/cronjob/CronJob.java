package com.ucsf.cronjob;

import com.opencsv.CSVReader;
import com.ucsf.model.Question;
import com.ucsf.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;


@Component
public class CronJob {

    @Autowired
    QuestionRepository questionRepository;


    //@Autowired
    @Scheduled(cron = "0 0 15 * * *")
    public void readQuestionsFrmCsvToDb() {

        System.out.println("Scheduled questions read from csv to db.");

        // Deleting all questions from DB first.
        //questionRepository.deleteAll();
        //Adding all questions from csv
        readCsvNAddQuestions("/Users/ashishsecrets/Downloads/Screening.csv");

        //


    }

    // Java code to illustrate reading a
    // CSV file line by line

    public void readCsvNAddQuestions(String file)
    {

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            ArrayList<String> content = new ArrayList<>();
            int count = 0;
            int quesNumber = 0;

            int ignoreCellCount = 3;
            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    count++;
                    if(count > ignoreCellCount){
                    content.add(cell);
                    System.out.println("Added a cell");}
                }

                if((count-ignoreCellCount) > quesNumber*(count-ignoreCellCount))
                addQuestions(content, count-ignoreCellCount);
                System.out.println("Added question" + (count-ignoreCellCount));
                quesNumber++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addQuestions(ArrayList<String> content, int position) throws Exception {

        Question ques = new Question();

        ques.setDescription(content.get(position));
        switch (content.get(position + 1)) {
            case "OPEN_ENDED":
                ques.setQuestionType(Question.QuestionType.OPEN_ENDED);
                break;
            case "DICHOTOMOUS":
                ques.setQuestionType(Question.QuestionType.DICHOTOMOUS);
                break;
            case "MULTIPLE_CHOICE":
                ques.setQuestionType(Question.QuestionType.MULTIPLE_CHOICE);
                break;
            case "CHECKBOX":
                ques.setQuestionType(Question.QuestionType.DROPDOWN);
                break;
            case "SCROLL_BAR":
                ques.setQuestionType(Question.QuestionType.RATING_SCALE);
                break;
        }
        ques.setChoice(content.get(position+2));
        ques.setEnabled(true);
        ques.setCreatedBy("1");
        ques.setCreatedDate(new Date());
        ques.setLastModifiedBy("1");
        ques.setLastModifiedDate(new Date());
        questionRepository.save(ques);
        System.out.println("Written into db");
    }
}
