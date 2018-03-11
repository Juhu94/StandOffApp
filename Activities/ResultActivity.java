package com.mah.simon.standoffapp;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ResultActivity extends AppCompatActivity {

    private TextView tvSTime;
    private TextView tvETime;
    private TextView tvTime;
    private TextView tvReact;
    private TextView tvAcc;
    private TextView tvResult;
    private EditText etName;
    private TextView tvP2STime;
    private TextView tvP2ETime;
    private TextView tvP2Time;
    private TextView tvP2React;
    private TextView tvP2Acc;
    private TextView tvWinOrLose;

    private DBHandler dbHandler;

    private Context context;

    private long time;
    private long react;
    private double acc;
    private long p2time;
    private long p2react;
    private double p2acc;

    private Button btnMenu;
    private Button btnSaveScore;

    private int totalPoints;
    private int P2TotalPoints;

    private Score score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        context = getApplicationContext();

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSTime = (TextView) findViewById(R.id.tvStartTime);
        tvETime = (TextView) findViewById(R.id.tvEndTime);
        tvAcc = (TextView) findViewById(R.id.tvAcc);
        tvReact = (TextView) findViewById(R.id.tvReac);
        tvResult = (TextView) findViewById(R.id.tvResult);
        etName = (EditText) findViewById(R.id.etName);
        tvP2STime = (TextView) findViewById(R.id.tvPlayerTwoStartTime);
        tvP2ETime = (TextView) findViewById(R.id.tvPlayerTwoEndTime);
        tvP2Time = (TextView) findViewById(R.id.tvPlayerTwoTotalTime);
        tvP2React = (TextView) findViewById(R.id.tvPlayerTwoReac);
        tvP2Acc = (TextView) findViewById(R.id.tvPlayerTwoAcc);
        tvWinOrLose = (TextView) findViewById(R.id.tvWinOrLose);

        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnSaveScore = (Button) findViewById(R.id.btnSaveScore);
        btnSaveScore.setEnabled(true);
        etName.setEnabled(true);

        Intent intent = getIntent();

        dbHandler = new DBHandler(this);

        time = intent.getLongExtra("totalTime", 0);
        react = intent.getLongExtra("reaction", 0);
        acc = intent.getDoubleExtra("accurate", 0);

        tvSTime.setText(Long.toString(intent.getLongExtra("timeStart", 0)));
        tvETime.setText(Long.toString(intent.getLongExtra("timeEnd", 0)));
        tvTime.setText(Long.toString(time));       //the draw time
        tvReact.setText(Long.toString(react));
        tvAcc.setText(Double.toString(acc));

        if (intent.getBooleanExtra("multiplayer", false)){
            updatePlayerTwo(intent);
        }

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainMenu();
            }
        });

        totalPoints = (100 - (int)Math.abs(acc * 100));
        totalPoints = (totalPoints + (375 - (int) react));
        totalPoints = (totalPoints + (375 - (int) time));

        tvResult.setText(Integer.toString(totalPoints));        // ca 300 points max

        btnSaveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etName.getText().toString().equals("")) {

                    String cal = Calendar.getInstance().getTime().toString();
                    String date = cal.substring(3, 16) + " " + cal.substring(29);

                    score = new Score(totalPoints, etName.getText().toString(), date);

                    dbHandler.addScore(score);
                    btnSaveScore.setEnabled(false);
                    etName.setEnabled(false);
                    Toast.makeText(context, "Score saved!", Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(context, "You must enter a name if you want to save your score.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void updatePlayerTwo(Intent intent){
        p2time = intent.getLongExtra("p2timeEnd", 0) - intent.getLongExtra("p2timeStart", 0);
        p2react = intent.getLongExtra("p2timeStart", 0) - intent.getLongExtra("p2reaction", 0);
        p2acc = intent.getDoubleExtra("p2accurate", 0);

        tvP2STime.setText(Long.toString(intent.getLongExtra("p2timeStart", 0)));
        tvP2ETime.setText(Long.toString(intent.getLongExtra("p2timeEnd", 0)));
        tvP2Time.setText(Long.toString(p2time));
        tvP2React.setText(Long.toString(p2react));
        tvP2Acc.setText(Double.toString(p2acc));

        P2TotalPoints = (100 - (int)Math.abs(p2acc * 100));
        P2TotalPoints = (totalPoints + (375 - (int) p2react));
        P2TotalPoints = (totalPoints + (375 - (int) p2time));

        if (P2TotalPoints < totalPoints){
            tvWinOrLose.setText(R.string.tvWin);
        }if(P2TotalPoints > totalPoints){
            tvWinOrLose.setText(R.string.tvLose);
        }else{
            tvWinOrLose.setText(R.string.tvDraw);
        }
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
