package com.mah.simon.standoffapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private Button btnPlay;
    private Button btnHighscore;
    private Button btnHelp;
    private Button btnDuel;

    private boolean BTPresent = false;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button)this.findViewById(R.id.btnPlay);
        btnHighscore = (Button)this.findViewById(R.id.btnHighScore);
        btnHelp = (Button)this.findViewById(R.id.btnHelp);
        btnDuel = (Button)findViewById(R.id.btnDuel);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("multiplayer", false);
                startActivity(intent);
                Log.d(TAG, ": PlayActivity");
            }
        });

        btnDuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BTPresent){
                    multiplayerMode();
                }else{
                    Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth, Can't access multiplayer mode.", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnHighscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HighscoreActivity.class);
                startActivity(intent);
                Log.d(TAG, ": HighscoreActivity");
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                Log.d(TAG, ": HelpActivity");
            }
        });
    }

    @Override
    protected void onResume() {
        if(mBluetoothAdapter == null) {
            BTPresent = false;
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        }
        else{
            BTPresent = true;
        }
        super.onResume();
    }

    private void multiplayerMode() {
        Log.d(TAG, "multiplayerMode: Attempting to start a new activity 'deviceListActivity'");

        Intent deviceListActivityIntent = new Intent(this, DeviceListActivity.class);
        startActivity(deviceListActivityIntent);

    }
}
