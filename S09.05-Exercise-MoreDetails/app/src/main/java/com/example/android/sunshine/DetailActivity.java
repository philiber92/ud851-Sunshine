/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import java.util.Date;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.*;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private static final String[] DATA_COLUMNS = new String[] {
            COLUMN_WEATHER_ID,
            COLUMN_DATE,
            COLUMN_DEGREES,
            COLUMN_MAX_TEMP,
            COLUMN_MIN_TEMP,
            COLUMN_WIND_SPEED,
            COLUMN_HUMIDITY,
            COLUMN_PRESSURE
    };

    private static final int LOADER_ID = 22;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

    private Uri mUri;

    private TextView mDate, mDescription, mHighLow, mHumidity, mWind, mPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDate = (TextView) findViewById(R.id.tv_weather_date);
        mDescription = (TextView) findViewById(R.id.tv_weather_decription);
        mHighLow = (TextView) findViewById(R.id.tv_weather_temperature);
        mHumidity = (TextView) findViewById(R.id.tv_weather_humidity);
        mWind = (TextView) findViewById(R.id.tv_weather_wind);
        mPressure = (TextView) findViewById(R.id.tv_weather_pressure);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mUri = intentThatStartedThisActivity.getData();
            if(mUri == null) throw new NullPointerException();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID) {
            return new CursorLoader(
                    this,
                    mUri,
                    DATA_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            mDate.setText(new Date(data.getLong(data.getColumnIndex(COLUMN_DATE))).toString());
            String description = SunshineWeatherUtils.getStringForWeatherCondition(
                    this, data.getInt(data.getColumnIndex(COLUMN_WEATHER_ID)));
            mDescription.setText(description);

            double high = data.getDouble(data.getColumnIndex(COLUMN_MAX_TEMP));
            double low = data.getDouble(data.getColumnIndex(COLUMN_MIN_TEMP));
            mHighLow.setText(SunshineWeatherUtils.formatHighLows(this, high, low));

            double humidity = data.getDouble(data.getColumnIndex(COLUMN_HUMIDITY));
            mHumidity.setText(String.valueOf(humidity));

            double pressure =  data.getDouble(data.getColumnIndex(COLUMN_PRESSURE));
            mPressure.setText(String.valueOf(pressure));

            float wind = data.getFloat(data.getColumnIndex(COLUMN_WIND_SPEED));
            float degrees = data.getFloat(data.getColumnIndex(COLUMN_DEGREES));
            mWind.setText(SunshineWeatherUtils.getFormattedWind(this, wind, degrees));

            mForecastSummary = description;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}