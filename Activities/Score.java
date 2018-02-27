package com.example.vaskslem.uppgift2;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by VaskSlem on 2018-02-23.
 */

public class Score {

    int id;
    int score;
    String name;

    public Score(int id, int score, String name){
        this.id = id;
        this.score = score;
        this.name = name;
    }

    public Score(){

    }

    public int getId(){

        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }


    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String Name){
        this.name = name;
    }

}
