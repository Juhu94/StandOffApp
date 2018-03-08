package com.example.julian.sensor_standoffapp;

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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by julia on 2018-03-06.
 */

public class BluetoothConnectedThread extends Thread {

    private static final String TAG = "ConnectedThread";
    private Handler mHandler;

    private final BluetoothSocket mmSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private boolean host = false;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private Context context;

    public BluetoothConnectedThread(BluetoothSocket socket, Context context, boolean host) {
        mmSocket = socket;
        this.context = context;
        this.host = host;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
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
                Log.d(TAG, "MESSAGE RECEIVED....");
                messageHandler(numBytes);
              //  messageHandler(incMessage);
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    public boolean getHostStatus(){
        return this.host;
    }

    public void write(byte[] bytes) {
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int message){
        try{
            mOutputStream.write(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void messageHandler(int message){
        Log.d(TAG, String.valueOf(message));
        switch (message){
            case Constants.CONNECTED_TRUE:
                Log.d(TAG, "Start PlayActivity....");
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("multiplayer", true);
                context.startActivity(intent);
                break;
        }
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
