package com.example.erikj.sensor_standoffapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;


public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView webView = (WebView) findViewById(R.id.wvGifHelp);
        webView.loadUrl("file:android_res/drawable/draw.gif");

    }
}
