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
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;

public class PlayActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "PlayActivity";

    private Context context;

    private double average;
    private LinkedList<Float> accAverage = new LinkedList<Float>();

    private boolean gyroTrigger = false;
    private boolean proxyTrigger = false;
    private boolean accTrigger = false;
    private boolean sigTrigger = false;
    private boolean abortTrigger = false;
    private boolean multiplayer = false;
    private boolean proxyNewRead;
    private static boolean playerTwoPointsArrived = false;
    private static boolean finalResultHasArrived = false;


    private long timeStamp;
    private long timeReact;
    private long timeStart;
    private long timeEnd;

    private static int opponentsTotalPoints = 0;
    private static int mTotalPoints = -1;
    private static int hostPoints = -1;
    private static int finalResultFromHost = -1;

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
        
        proxyNewRead = true;
        
        WebView webView = (WebView) findViewById(R.id.wvGif);
        webView.loadUrl("file:android_res/drawable/draw.gif");

        Intent intent = getIntent();
        multiplayer = intent.getBooleanExtra("multiplayer", false);
        if (multiplayer){
            timeStamp = intent.getLongExtra("timeStamp", 0);
            startCoundown((int)((timeStamp - System.currentTimeMillis())/1000));
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
    protected void onResume() {
        super.onResume();
        register();
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

    public synchronized void startGame(){
        Log.d(TAG, "START GAME!");
        mTotalPoints = -1;
        opponentsTotalPoints = -1;
        finalResultFromHost = -1;
        finalResultHasArrived = false;
        playerTwoPointsArrived = false;
        if(!abortTrigger) {
            timeReact = System.currentTimeMillis();
            proxyNewRead = true;
            proxyTrigger = true;
            vibrator.vibrate(500);
        }else {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            Toast.makeText(context,"No cheating! ",Toast.LENGTH_SHORT).show();
            if(multiplayer){
                if(!mConnectedThread.getHostStatus()){
                    mConnectedThread.write(0);
                    mConnectedThread.cancel();
                }else if (mConnectedThread.getHostStatus()){
                    while(!playerTwoPointsArrived );
                    mConnectedThread.write(0);
                    mConnectedThread.cancel();
                }
            }
        }
    }

    public synchronized void proxyTestStart(){
        timeStart = System.currentTimeMillis();
        gyroTrigger = true;
        average = 0.0f;
        accAverage.clear();
    }

    public synchronized void proxyTestEnd(){
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
                //printResults();
                finishedGame();
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

    private synchronized void finishedGame(){
        if(multiplayer){
            Log.d(TAG, timeStart + "\n" + timeEnd + "\n" + timeReact + "\n" + average);
            if(mConnectedThread != null) {
                for (int i = 0; i < accAverage.size(); i++){               //calculates the average accuracy
                    average += accAverage.get(i);
                }
                average = average/ accAverage.size();
                mTotalPoints = (100 - (int)Math.abs(average * 100));
                Log.d(TAG, "mTotalPoints + average: " +mTotalPoints);
                mTotalPoints = (mTotalPoints + (50 - (int) ((timeStart - timeReact) / 6)));
                Log.d(TAG, "mTotalPoints + totalTime: " +mTotalPoints);
                Log.d(TAG, "totalTime: " + (int) ((timeStart - timeReact) / 6));
                mTotalPoints = (mTotalPoints + (105 - (int) ((timeEnd - timeStart) / 6)));
                Log.d(TAG, "mTotalPoints + reacTime: " +mTotalPoints);
                Log.d(TAG, "totalTime: " + (int) ((timeEnd - timeStart) / 6));
                if(mTotalPoints <= 0){
                    mTotalPoints = 0;
                }
                Log.d(TAG, "Total points: " +mTotalPoints);
            }else{
                Log.d(TAG,"Av någon anledning är bluetoothConnectedThread null");
            }
            if(!mConnectedThread.getHostStatus()){
                mConnectedThread.write(mTotalPoints);
                printResults();
            }else if (mConnectedThread.getHostStatus()){
                printResults();
            }
        }else if(!multiplayer){
            for (int i = 0; i < accAverage.size(); i++){               //calculates the average accuracy
                average += accAverage.get(i);
            }
            average = average/ accAverage.size();
            mTotalPoints = (100 - (int)Math.abs(average * 100));
            Log.d(TAG, "mTotalPoints + average: " +mTotalPoints);
            mTotalPoints = (mTotalPoints + (50 - (int) ((timeStart - timeReact) / 6)));
            Log.d(TAG, "mTotalPoints + totalTime: " +mTotalPoints);
            Log.d(TAG, "totalTime: " + (int) ((timeStart - timeReact) / 6));
            mTotalPoints = (mTotalPoints + (105 - (int) ((timeEnd - timeStart) / 6)));
            Log.d(TAG, "mTotalPoints + reacTime: " +mTotalPoints);
            Log.d(TAG, "totalTime: " + (int) ((timeEnd - timeStart) / 6));
            if(mTotalPoints <= 0){
                mTotalPoints = 0;
            }
            Log.d(TAG, "Total points: " +mTotalPoints);
            printResults();
        }
    }

    public synchronized static void setOpponentsPoints(int p2points){
        opponentsTotalPoints = p2points;
        playerTwoPointsArrived = true;
    }

    public synchronized static void setFinalResult(int pointsFromHost){
        hostPoints = pointsFromHost;
        Log.d(TAG, "Host points: " +hostPoints);
        if(mTotalPoints > hostPoints){
            finalResultFromHost = Constants.YOU_WON;
            Log.d(TAG, "YOU WON");
        }else if(mTotalPoints < hostPoints){
            finalResultFromHost = Constants.YOU_LOST;
            Log.d(TAG, "YOU LOST");
        }else{
            finalResultFromHost = Constants.DRAW;
            Log.d(TAG, "DRAW");
        }
        finalResultHasArrived = true;
        Log.d(TAG, "finalResultHasArrived: true");
    }

//    private void sendData(){
//        Log.d(TAG, timeStart + "\n" + timeEnd + "\n" + timeReact + "\n" + average);
//        if(mConnectedThread != null) {
//            int totalPoints = (100 - (int)Math.abs(average * 100));
//            totalPoints = (totalPoints + (375 - (int) (timeStart - timeReact)));
//            totalPoints = (totalPoints + (375 - (int) (timeEnd - timeStart)));
//            Log.d(TAG, "Total points: " +totalPoints);
//            mConnectedThread.write(totalPoints);
//        }else{
//            Log.d(TAG,"Av någon anledning är bluetoothConnectedThread null");
//        }
//    }

    private synchronized void printResults() {

       /* for (int i = 0; i < accAverage.size(); i++){               //calculates the average accuracy
            average += accAverage.get(i);
        }
        average = average/ accAverage.size(); */
        Intent intent = new Intent(PlayActivity.this, ResultActivity.class); //New intent to start result activity
        intent.putExtra("timeStart", timeStart);
        intent.putExtra("timeEnd", timeEnd);
        intent.putExtra("totalTime", timeEnd - timeStart);      //the draw time
        intent.putExtra("reaction", timeStart - timeReact);     //the react time
        intent.putExtra("accurate", average);
        intent.putExtra("myPoints", mTotalPoints);
        //the accuracy
        /*if(multiplayer){
           // mConnectedThread.setDataBool(false);
            sendData();
            Long myWhait = System.currentTimeMillis() + 5000;
           while (!mConnectedThread.getData() && (System.currentTimeMillis() > myWhait)){

           }
           intent.putExtra("multiplayer", true);
           intent.putExtra("totalPoints", mConnectedThread.getP2Points());
            Log.d(TAG, "DATA framme hämtar data " + mConnectedThread.getP2Points());
        }*/
        if(multiplayer){
            if(mConnectedThread.getHostStatus()){
                while(!playerTwoPointsArrived );
                Log.d(TAG, "OPPONENTS POINTS HAS ARRIVED: " +opponentsTotalPoints);
                int finalResult = -1;
                if(mTotalPoints > opponentsTotalPoints){
                    finalResult = Constants.YOU_WON;
                    Log.d(TAG, "YOU WON");
                }else if(mTotalPoints < opponentsTotalPoints){
                    finalResult = Constants.YOU_LOST;
                    Log.d(TAG, "YOU LOST");
                }else{
                    finalResult = Constants.DRAW;
                    Log.d(TAG, "DRAW");
                }
                Log.d(TAG, "OPPONENTS POINTS: " +opponentsTotalPoints);
                intent.putExtra("finalResult", finalResult);
                intent.putExtra("opponentsPoints: ", opponentsTotalPoints);
                intent.putExtra("multiplayer", true);
                mConnectedThread.write(mTotalPoints);
            }else if(!mConnectedThread.getHostStatus()){
                Log.d(TAG, "WAITING FOR FINAL RESULT IN A LOOP");
                while(!finalResultHasArrived);
                Log.d(TAG, "FINAL RESULT HAS ARRIVED");
                intent.putExtra("finalResult", finalResultFromHost);
                intent.putExtra("hostPoints", hostPoints);
                intent.putExtra("multiplayer", true);
                Log.d(TAG, "" +opponentsTotalPoints);
            }
        }
        if(multiplayer){
            mConnectedThread.cancel();
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
            Log.e(TAG, "current time: " + System.currentTimeMillis() + " timestamp: " + timeStamp);
            if(event.values[0] < mSensorProxy.getMaximumRange() && !proxyTrigger && proxyNewRead) {
                if(!multiplayer) {
                    startCoundown();
                    Toast.makeText(context, "Countdown started, Be ready", Toast.LENGTH_SHORT).show();
                    abortTrigger = false;
                    proxyNewRead = false;

                }else{
                    abortTrigger = false;
                    proxyNewRead = false;
                }

            }else if(event.values[0] > 0 && proxyTrigger && !abortTrigger) {
                proxyTestStart();
                proxyNewRead = false;

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
