package com.example.julia.sensor_standoffapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import static android.R.attr.button;

public class PlayActivity extends AppCompatActivity implements SensorEventListener{

    private Button btnResult;

    Context context;

    TextView time;
    TextView STime;
    TextView ETime;
    TextView acc;
    TextView reac;
    Button button;

    double avrage;
    LinkedList<Float> accAvreage = new LinkedList<Float>();

    boolean gyroTrigger = false;
    boolean proxyTrigger = false;
    boolean accTrigger = false;
    boolean sigTrigger = false;
    boolean countdown = false;

    long timeStart;
    long timeEnd;

    SensorManager mSensormaneger;
    Sensor mSensorAcc;
    Sensor mSensorSig;
    Sensor mSensorGyro;
    Sensor mSensorProxy;
    private Sensor proximitySensor;

    private CountDownTimer countDownTimer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        context = this;

        time = (TextView) findViewById(R.id.tvTime);
        STime = (TextView) findViewById(R.id.tvStartTime);
        ETime = (TextView) findViewById(R.id.tvEndTime);
        acc = (TextView) findViewById(R.id.tvAcc);
        reac = (TextView) findViewById(R.id.tvReac);
        button = (Button) findViewById(R.id.btn);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        btnResult = (Button)this.findViewById(R.id.btnResult);

        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayActivity.this, ResultActivity.class);
                startActivity(intent);
                Log.d("New Activity", ": PlayActivity");
            }
        });

        mSensormaneger = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        if (mSensormaneger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensorAcc = mSensormaneger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //value 2
        }

        if (mSensormaneger.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null){
            mSensorSig = mSensormaneger.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        }else {
            Toast.makeText(context, "SIGNIFICANT_MOTION sensor is missing.", Toast.LENGTH_LONG).show();
        }

        if (mSensormaneger.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mSensorGyro = mSensormaneger.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //value 3
        }

        //TODO Behöver kolla proximitySensor också, ta bort den andra proximitySensorn?
        if (mSensormaneger.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensorProxy = mSensormaneger.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            proximitySensor = mSensormaneger.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxyTrigger = true;
                //sigTrigger = true;
                register();
            }
        });

        register();
    }

    public void unRegister(){
        mSensormaneger.unregisterListener((SensorEventListener) context);
    }

    public void register(){
        mSensormaneger.registerListener((SensorEventListener) context, mSensorAcc, mSensormaneger.SENSOR_DELAY_UI);
        mSensormaneger.registerListener((SensorEventListener) context, mSensorSig, mSensormaneger.SENSOR_DELAY_UI);
        mSensormaneger.registerListener((SensorEventListener) context, mSensorGyro, mSensormaneger.SENSOR_DELAY_UI);
        mSensormaneger.registerListener((SensorEventListener) context, mSensorProxy, mSensormaneger.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensormaneger.unregisterListener(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSensormaneger.unregisterListener(this);
        time = null;
        acc = null;
        reac = null;
        button = null;
        mSensormaneger = null;
        mSensorSig = null;
        mSensorAcc = null;
        accAvreage = null;
    }

    public void proxyTestStart(long time){
        timeStart = time;
        gyroTrigger = true;
        avrage = 0.0f;
        accAvreage.clear();
    }

    public void proxyTestEnd(long time){
        Toast.makeText(context,"proxyTestEnd",Toast.LENGTH_SHORT).show();
        timeEnd = time;
        proxyTrigger = false;
        gyroTrigger = false;
        accTrigger = true;
        Handler h = new Handler();      //will execute the runnable code after the given delay
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"HANDLER",Toast.LENGTH_SHORT).show();
                unRegister();

                printResults();
            }
        },200);
    }

    public void sigTestStart(long time){
        timeStart = time;
        gyroTrigger = true;
        avrage =0.0f;
        accAvreage.clear();
    }

    public void sigTestEnd(long time){
        timeEnd = time;
        sigTrigger = false;
        gyroTrigger = false;
        accTrigger = true;
        Handler h = new Handler();      //will execute the runnable code after the given delay
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                unRegister();
                printResults();
            }
        },200);
    }

    private void startCoundown() {


        countDownTimer = new CountDownTimer(3 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                vibrator.vibrate(500);
            }
        };

        countDownTimer.start();
    }

    private void printResults() {
        STime.setText(Long.toString(timeStart));
        ETime.setText(Long.toString(timeEnd));
        time.setText(Long.toString(timeEnd - timeStart));
        for (int i = 0; i < accAvreage.size(); i++){       //calculates the avrage
            avrage += accAvreage.get(i);
        }
        avrage = avrage/accAvreage.size();
        acc.setText(String.valueOf(avrage));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (accTrigger && event.sensor.getType() == mSensorAcc.getType()){
            accAvreage.add(event.values[1]);
        }


        if (event.sensor.getType() == mSensorSig.getType() && sigTrigger){
            //sigTestStart(event.timestamp);
        }


        if (event.sensor.getType() == mSensorGyro.getType() && gyroTrigger) {
            if (event.values[2] > -0.3 && event.values[2] < 0.3) {
                proxyTestEnd(event.timestamp);
                Toast.makeText(context,"END",Toast.LENGTH_SHORT).show();
                //sigTestEnd(event.timestamp);
            }
        }

        if (event.sensor.getType() == mSensorProxy.getType() && proxyTrigger) {
            if (event.values[0] > 0) {
                proxyTestStart(event.timestamp);
                Toast.makeText(context,"START",Toast.LENGTH_SHORT).show();
            }
        }

        if(event.sensor.getType() == mSensorProxy.getType() && !proxyTrigger){

            if(event.values[0] < mSensorProxy.getMaximumRange()) {
                startCoundown();
                Toast.makeText(context,"Countdown started, Be ready",Toast.LENGTH_SHORT).show();
                proxyTrigger = true;

            } else {
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
