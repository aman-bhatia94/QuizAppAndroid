package com.example.quizapp;

public class AnswerResolver {

    private static int passCount = 0;
    private static int failCount = 0;


    public static int getPassCount() {
        return passCount;
    }

    public static void setPassCount(int passCount) {
        AnswerResolver.passCount = passCount;
    }

    public static int getFailCount() {
        return failCount;
    }

    public static void setFailCount(int failCount) {
        AnswerResolver.failCount = failCount;
    }

    public boolean multipleResolver(String answer){

        if(!answer.equalsIgnoreCase("Metallica")){
            return false;
        }
        else{
            return true;
        }
    }



    public boolean blankResolver(String answer){
        if(!answer.equalsIgnoreCase("James Hetfield")){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean multipleResolver2(String answer2) {

        if(!answer2.equalsIgnoreCase("Kirk Hammet")){
            return false;
        }
        else{
            return true;
        }
    }
}
