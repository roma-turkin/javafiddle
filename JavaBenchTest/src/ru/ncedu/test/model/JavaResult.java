/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.test.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kharichkin
 */
public class JavaResult {
    
    public enum Severity{
        INFO, WARN, ERROR, FATAL;
    }
    
    private String resultMessage;
    private String fullResults;
    private Severity severity;
    private int mark;
    private String userInput;
    
    private List<JavaSubResult> subResults = new ArrayList<>();

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
    
    
    
    public void addSubResult(JavaSubResult subResult){
        subResults.add (subResult);
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getFullResults() {
        return fullResults;
    }

    public void setFullResults(String fullResults) {
        this.fullResults = fullResults;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    
    
    
}
