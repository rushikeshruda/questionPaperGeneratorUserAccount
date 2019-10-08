package com.example.rushikesh.qpguseraccount;

/**
 * Created by Rushikesh on 05/04/2018.
 */

public class Question {
    public String questionId;
    public String questionText;
    public String questionLevel;

    public Question(){

    }

    public Question(String questionId, String questionText, String questionLevel) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionLevel = questionLevel;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionLevel(){return questionLevel;}
}
