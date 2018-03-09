package com.example.erikj.sensor_standoffapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HighscoreActivity extends AppCompatActivity {

    private ListView lvScoreList;
    private TextView tvHighscore;

    private ArrayAdapter arrayAdapter;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        dbHandler = new DBHandler(this);

        tvHighscore = (TextView) findViewById(R.id.tvHighscore);
        lvScoreList = (ListView) findViewById(R.id.lvScoreList);
        String highscore = dbHandler.getHighScore();

        if(highscore.equals(null + " | " + null + " | " + null)){
            tvHighscore.setText("No available scores to show");
        } else {
            tvHighscore.setText("Highscore:\n" +highscore);
        }
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbHandler.getAllScores());
        lvScoreList.setAdapter(arrayAdapter);

    }
}
