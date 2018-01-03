package com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.List;

/**
 * Created by Philipp on 03.01.2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_sunshine);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        int count = getPreferenceScreen().getPreferenceCount();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        for(int i = 0; i < count; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if(!(preference instanceof CheckBoxPreference)) {
                Object value = sharedPreferences.getAll().get(preference.getKey());
                setPreferenceSummary(preference, value);
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_location_key))) {
            setPreferenceSummary(
                    getPreferenceScreen().findPreference(key),
                    sharedPreferences.getString(key, getString(R.string.pref_location_default))
            );
        } else if(key.equals(getString(R.string.pref_units_key))) {
            setPreferenceSummary(
                    getPreferenceScreen().findPreference(key),
                    sharedPreferences.getString(key, getString(R.string.pref_units_default))
            );
        }
    }

    private void setPreferenceSummary(Preference preference, Object object) {
        if(preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary((String) object);
        } else if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        }
    }
}