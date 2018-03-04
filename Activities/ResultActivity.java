package com.mah.simon.standoffapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView tvSTime;
    private TextView tvETime;
    private TextView tvTime;
    private TextView tvReact;
    private TextView tvAcc;
    private TextView tvResult;

    private long time;
    private long react;
    private double acc;

    private Button btn;

    private int totalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSTime = (TextView) findViewById(R.id.tvStartTime);
        tvETime = (TextView) findViewById(R.id.tvEndTime);
        tvAcc = (TextView) findViewById(R.id.tvAcc);
        tvReact = (TextView) findViewById(R.id.tvReac);
        tvResult = (TextView) findViewById(R.id.tvResult);

        btn = (Button) findViewById(R.id.btnMenu);

        Intent intent = getIntent();

        time = intent.getLongExtra("totalTime", 0);
        react = intent.getLongExtra("reaction", 0);
        acc = intent.getDoubleExtra("accurate", 0);

        tvSTime.setText(Long.toString(intent.getLongExtra("timeStart", 0)));
        tvETime.setText(Long.toString(intent.getLongExtra("timeEnd", 0)));
        tvTime.setText(Long.toString(time));       //the draw time
        tvReact.setText(Long.toString(react));
        tvAcc.setText(Double.toString(acc));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainMenu();
            }
        });

        totalPoints = (100 - (int)Math.abs(acc * 100));
        totalPoints = (totalPoints + (375 - (int) react));
        totalPoints = (totalPoints + (375 - (int) time));

        tvResult.setText(Integer.toString(totalPoints));        // ca 300 points max
    }

    @Override
    public void onBackPressed(){        //makes the back button take you to main menu
        toMainMenu();
    }

    public void toMainMenu(){
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
