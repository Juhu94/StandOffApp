package com.example.julia.sensor_standoffapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by julia on 2018-03-06.
 */

public class BluetoothAcceptThread extends Thread{
    private static final String TAG = "AcceptThread";
    private static final String APP_NAME = "MY_APP";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private BluetoothAdapter mBluetoothAdapter;

    private final BluetoothServerSocket mmServerSocket;

    public BluetoothAcceptThread(){
        Log.d(TAG,"AcceptThread running");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothServerSocket tmp = null;
        try{
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
        }catch (IOException e){
            Log.e(TAG, "Socket's listen() method failed", e);
        }

        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;

        while (true){
            try{
                Log.d(TAG, "run: RFCOM server socket start.....");
                socket = mmServerSocket.accept();
                Log.d(TAG, "run: RFCOM AcceptThread server socket accepted connection");
            }catch (IOException e){
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            cancel();
        }
    }

    public void cancel() {
        try{
            mmServerSocket.close();
        }catch (IOException e ){
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
