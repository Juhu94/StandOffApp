package com.example.erikj.sensor_standoffapp;

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
    String date;

    public Score(int id, int score, String name){
        this.id = id;
        this.score = score;
        this.name = name;
    }

    public Score(int score, String name, String date){
        this.score = score;
        this.name = name;
        this.date = date;
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

    public String getDate(){
        return this.date;
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
