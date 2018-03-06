package com.example.julia.sensor_standoffapp;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private byte[] mmBuffer; // mmBuffer store for the stream

    public BluetoothConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
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

    /**
     * Read och write är skriva som så att dem skickar tillbaka en "constant" till den aktiva
     * activityn, där kollar man sedan genom en switch-sats vad man gjorde, t ex
     * "Constant.MESSAGE_READ eller Constant.MESSAGE_WRITE"
     * Detta behöver hanteras i den activityn som startar denna klassen. Vilken det ska vara eller
     * hur vi ska lösa det vet jag inte än. Kanske ska ändra om read och write funktionerna i denna
     * klassen istället.
     *
     * Den switch-satsen som behövs är ej implementerad för vet inte i vilken (kanske en ny)
     * activity som ska den ska ligga i.
     */

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs.
        while (true) {;
            try {
                // Read from the InputStream.
                numBytes = mInputStream.read(mmBuffer);
                Log.d(TAG, "MESSAGE RECEIVED....");
                // Send the obtained bytes to the UI activity.
             //   Message readMsg = mHandler.obtainMessage(Constants.MESSAGE_READ, numBytes, -1, mmBuffer);
                showMessage(numBytes);
             //   readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Share the sent message with the UI activity.
 /*           Message writtenMsg = mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, mmBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            mHandler.sendMessage(writeErrorMsg);
        } */
    }

    public void testMessage(){
        String test = "this is a test";
        byte[] bytes = test.getBytes(Charset.defaultCharset());
        write(bytes);
        Log.d(TAG,"MESSAGE SENT....");
    }

    private void showMessage(int bytes){
        byte[] buffer = new byte[1024];
        String incomingMessage = new String(String.valueOf(bytes));
        Log.d(TAG, "THE MESSAGE: " +incomingMessage);
    }
}
