package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherTextView = (TextView) findViewById(R.id.tv_detail_weather);

        Intent creator = getIntent();
        String weather = creator.getStringExtra(Intent.EXTRA_TEXT);

        if(weather != null) {
            mWeatherTextView.setText(weather);
        }
    }
}