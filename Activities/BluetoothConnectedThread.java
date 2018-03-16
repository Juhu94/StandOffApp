package com.mah.simon.standoffapp;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Julian§ on 2018-03-06.
 */

public class BluetoothConnectedThread extends Thread implements Serializable{

    private static final String TAG = "ConnectedThread";

    private final BluetoothSocket mmSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private boolean host = false;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private Context context;

    private boolean dataIn = false;

    private int P2Points = 0;

    public BluetoothConnectedThread(BluetoothSocket socket, Context context, boolean host) {
        mmSocket = socket;
        this.context = context;
        this.host = host;
        Log.d(TAG, "I AM HOST: " +this.host);
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.

        try {
            if(mmSocket != null){
                tmpIn = mmSocket.getInputStream();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            if(mmSocket != null){
                tmpOut = mmSocket.getOutputStream();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mInputStream = tmpIn;
        mOutputStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mInputStream.read();
              //  String incMessage = new String(mmBuffer, 0, numBytes);
                Log.d(TAG, "MESSAGE RECEIVED...." +numBytes);
                messageHandler(numBytes);
              //  messageHandler(incMessage);
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    public boolean getHostStatus(){
        return this.host;
    }

  /*  public void write(byte[] bytes) {
        try {
            dataIn =false; // <--- tabort?
            mOutputStream.write(bytes);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    public void write(int message){
        try{
            Log.d(TAG, "Skickar: " +message);
            mOutputStream.write(message);
            mOutputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

 /*   public void write(String str){
        try {
            write(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    } */

    private void messageHandler(int message){
        Log.d(TAG, Integer.toString(message));
        if(message == Constants.CONNECTED_TRUE){
            Log.d(TAG, "Start PlayActivity....");
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("multiplayer", true);
            intent.putExtra("timeStamp", System.currentTimeMillis() + 5000);
            context.startActivity(intent);
            PlayActivity.setConnectedThread(this);
        }else{
            if(host){
                P2Points = message;
                PlayActivity.setOpponentsPoints(P2Points);
            }else if(!host){
                PlayActivity.setFinalResult(message);
            }

        }
       /* switch (message){
            case Constants.CONNECTED_TRUE:
                Log.d(TAG, "Start PlayActivity....");
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("multiplayer", true);
                intent.putExtra("timeStamp", System.currentTimeMillis() + 5000);
                context.startActivity(intent);
                PlayActivity.setConnectedThread(this);
                break;
            default:    //handels the data when it arrives
                if (!dataIn){
                    P2Points = message;
                    dataIn = true;
                    Log.d(TAG, "Player 2 points: " + message);
                }
                break;
        }*/
    }

    public void setDataBool(boolean tOrf){
        dataIn = tOrf;
    }

    public Boolean getData(){ //to check if p2 data is available
        return dataIn;
    }
    public int getP2Points() { //to get player 2 points
        return P2Points;
    }

    public void cancel() {
        try {
            mInputStream.close();
            mOutputStream.close();
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }

    public void testLog(){
        Log.d(TAG, "TEST: ANROPAT FRÅN PLAYACTIVITYN");
    }
/*
    private void messageHandler(String incMessage){
        Log.d(TAG,"The message: " +incMessage);
        switch (incMessage){
            case "CONNECTED":
                Log.d(TAG, "Start PlayActivity....");
                Intent intent = new Intent(context, PlayActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    public void testMessage() throws UnsupportedEncodingException {
        String test = "hej";
        byte[] bytes = test.getBytes("UTF-8");
        write(bytes);
        Log.d(TAG,"MESSAGE SENT....");
    }

    private void showMessage(String incMessage){
        byte[] buffer = new byte[1024];
        Log.d(TAG, "THE MESSAGE: " +incMessage);
    }
    */
}
