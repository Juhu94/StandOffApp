package com.mah.simon.standoffapp;

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
import android.widget.Toast;

import java.util.LinkedList;

public class PlayActivity extends AppCompatActivity implements SensorEventListener{

    private Context context;

    private double average;
    private LinkedList<Float> accAverage = new LinkedList<Float>();

    private BluetoothConnectedThread bluetoothConnectedThread;

    private boolean gyroTrigger = false;
    private boolean proxyTrigger = false;
    private boolean accTrigger = false;
    private boolean sigTrigger = false;
    private boolean abortTrigger = false;
    private boolean multiplayer = false;

    private long timeStamp;
    private long timeReact;
    private long timeStart;
    private long timeEnd;

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorSig;
    private Sensor mSensorGyro;
    private Sensor mSensorProxy;

    private CountDownTimer countDownTimer;
    private Vibrator vibrator;
    private static BluetoothConnectedThread mConnectedThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        context = this;

        Intent intent = getIntent();
        multiplayer = intent.getBooleanExtra("multiplayer", false);
        if (multiplayer){
            timeStamp = intent.getLongExtra("timeStamp", 0);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //value 2
        }
        else {
            Toast.makeText(context, "ACCELEROMETER sensor is missing.", Toast.LENGTH_LONG).show();
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null){
            mSensorSig = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        }else {
            Toast.makeText(context, "SIGNIFICANT_MOTION sensor is missing.", Toast.LENGTH_LONG).show();
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //value 3
        }else {
            Toast.makeText(context, "GYROSCOPE sensor is missing.", Toast.LENGTH_LONG).show();
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensorProxy = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else {
            Toast.makeText(context, "PROXIMITY sensor is missing.", Toast.LENGTH_LONG).show();
        }

        register();
    }

    public void unRegister(){
        mSensorManager.unregisterListener((SensorEventListener) context);
    }

    public void register(){
        mSensorManager.registerListener((SensorEventListener) context, mSensorAcc, mSensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener((SensorEventListener) context, mSensorSig, mSensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener((SensorEventListener) context, mSensorGyro, mSensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener((SensorEventListener) context, mSensorProxy, mSensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mSensorSig = null;
        mSensorAcc = null;
        accAverage = null;
        countDownTimer = null;
        vibrator = null;
    }

    public void startGame(){
        if(!abortTrigger) {
            timeReact = System.currentTimeMillis();
            proxyTrigger = true;
            vibrator.vibrate(500);
        }else {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            Toast.makeText(context,"No cheating! ",Toast.LENGTH_SHORT).show();
        }
    }

    public void proxyTestStart(){
        timeStart = System.currentTimeMillis();
        gyroTrigger = true;
        average = 0.0f;
        accAverage.clear();
    }

    public void proxyTestEnd(){
        timeEnd = System.currentTimeMillis();
        proxyTrigger = false;
        gyroTrigger = false;
        accTrigger = true;
        Handler h = new Handler();      //will execute the runnable code after the given delay
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                unRegister();
                mSensorManager.registerListener((SensorEventListener) context, mSensorProxy, mSensorManager.SENSOR_DELAY_UI);
                printResults();
            }
        },200);
    }

/*    public void sigTestStart(){
        timeStart = System.currentTimeMillis();
        gyroTrigger = true;
        average =0.0f;
        accAverage.clear();
    }*/

/*    public void sigTestEnd(){
        timeEnd = System.currentTimeMillis();
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
    }*/

    private void startCoundown(int sec) {

        countDownTimer = new CountDownTimer(sec * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                startGame();
            }
        };

        countDownTimer.start();
    }

    private void startCoundown(){   //TODO change to random
        startCoundown(3);
    }

    private void sendData(){
        bluetoothConnectedThread.write((int)timeStart);
        bluetoothConnectedThread.write((int)timeEnd);
        bluetoothConnectedThread.write((int)timeReact);
        bluetoothConnectedThread.write(Double.toString(average));
    }

    private void printResults() {

        sendData();

        for (int i = 0; i < accAverage.size(); i++){               //calculates the average accuracy
            average += accAverage.get(i);
        }
        average = average/ accAverage.size();
        Intent intent = new Intent(PlayActivity.this, ResultActivity.class); //New intent to start result activity
        intent.putExtra("timeStart", timeStart);
        intent.putExtra("timeEnd", timeEnd);
        intent.putExtra("totalTime", timeEnd - timeStart);      //the draw time
        intent.putExtra("reaction", timeStart - timeReact);     //the react time
        intent.putExtra("accurate", average);                         //the accuracy
        if(multiplayer){
           while (!bluetoothConnectedThread.getData()){
               try {
                   wait(500);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           intent.putExtra("multiplayer", true);
           intent.putExtra("p2timeStart",bluetoothConnectedThread.getSTime());
           intent.putExtra("p2timeEnd",bluetoothConnectedThread.getETime());
           intent.putExtra("p2reaction",bluetoothConnectedThread.getRTime());
           intent.putExtra("p2accurate",bluetoothConnectedThread.getAvreage());
        }
        startActivity(intent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (accTrigger && event.sensor.getType() == mSensorAcc.getType()){
            accAverage.add(event.values[1]);
        }


        if (event.sensor.getType() == mSensorSig.getType() && sigTrigger){
            //sigTestStart(event.timestamp);
        }


        if (event.sensor.getType() == mSensorGyro.getType() && gyroTrigger) {
            if (event.values[2] > -0.3 && event.values[2] < 0.3) {
                proxyTestEnd();
                //sigTestEnd(event.timestamp);
            }
        }

        if(event.sensor.getType() == mSensorProxy.getType()){

            if(event.values[0] < mSensorProxy.getMaximumRange() && !proxyTrigger) {
                if(!multiplayer) {
                    startCoundown();
                    Toast.makeText(context, "Countdown started, Be ready", Toast.LENGTH_SHORT).show();
                    abortTrigger = false;

                }else if(multiplayer && (System.currentTimeMillis() > (timeStamp - 1000))) {
                    startCoundown(1);
                    abortTrigger = false;
                }else if(multiplayer && (System.currentTimeMillis() > timeStamp)){
                    abortTrigger = true;
                    startCoundown(1);
                }

            }else if(event.values[0] > 0 && proxyTrigger && !abortTrigger) {
                proxyTestStart();

            }else if(event.values[0] > 0) {
                abortTrigger = true;
            }
        }
    }

    public static void setConnectedThread(BluetoothConnectedThread thread){
        mConnectedThread = thread;
        mConnectedThread.testLog();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}