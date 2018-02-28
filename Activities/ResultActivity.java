package com.example.lukas.standoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView STime;
    private TextView ETime;
    private TextView time;
    private TextView react;
    private TextView acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        time = (TextView) findViewById(R.id.tvTime);
        STime = (TextView) findViewById(R.id.tvStartTime);
        ETime = (TextView) findViewById(R.id.tvEndTime);
        acc = (TextView) findViewById(R.id.tvAcc);
        react = (TextView) findViewById(R.id.tvReac);

        Intent intent = getIntent();
        STime.setText(Long.toString(intent.getLongExtra("timeStart", 0)));
        ETime.setText(Long.toString(intent.getLongExtra("timeEnd", 0)));
        time.setText(Long.toString(intent.getLongExtra("totalTime", 0)));       //the draw time
        react.setText(Long.toString(intent.getLongExtra("reaction", 0)));
        acc.setText(Double.toString(intent.getDoubleExtra("accurate", 0)));
    }
}
