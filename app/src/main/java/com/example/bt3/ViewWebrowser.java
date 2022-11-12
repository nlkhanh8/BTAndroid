package com.example.bt3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ViewWebrowser extends AppCompatActivity {
    WebView wvBrowser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_webrowser);
        wvBrowser = findViewById(R.id.wvBrowser);
        WebSettings settings = wvBrowser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("das");
        String url = bundle.getString("linkMore");
        System.out.println(url);
        wvBrowser.loadUrl(url);
    }
}