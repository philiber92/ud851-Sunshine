package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;


import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.*;

public class SunshineSyncTask {

    synchronized public static void syncWeather(Context context) {
        try {
            URL url = NetworkUtils.getUrl(context);
            ContentValues[] contentValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, NetworkUtils.getResponseFromHttpUrl(url));

            if(contentValues == null || contentValues.length == 0) return;

            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.delete(CONTENT_URI, null, null);
            contentResolver.bulkInsert(CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}