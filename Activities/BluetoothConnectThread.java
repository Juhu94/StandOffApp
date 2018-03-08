package com.example.julian.sensor_standoffapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by julia on 2018-03-06.
 */

public class BluetoothConnectThread extends Thread {

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectedThread mConnectedThread;

    private static final String TAG = "ConnectThread";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private Context context;

    public BluetoothConnectThread(Context context, BluetoothDevice device){
        Log.d(TAG,"ConnectThread running");
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        mDevice = device;
        try{
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){
            Log.e(TAG, "Socket's create() method failed", e);
        }

        mSocket = tmp;
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try{
            mSocket.connect();
            Log.d(TAG, "run: ConnectedThread connected");
        }catch (IOException e) {
            Log.d(TAG, "run: Socket closed, could not connect");
            try {
                mSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        //TODO Ansluten, ny tråd ska skapas!
        mConnectedThread = new BluetoothConnectedThread(mSocket, context, false);
        mConnectedThread.start();
        connected();
    }

    private void connected() {
        int message = Constants.CONNECTED_TRUE;
        mConnectedThread.write(message);

        Log.d(TAG, "MESSAGE SENT....");
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra("multiplayer", true);
        context.startActivity(intent);
    }

    public void cancel() {
        try{
            mSocket.close();
        }catch (IOException e){
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
