package com.example.mahes_000.sunshine_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by mahes_000 on 5/12/2016.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Here we add the general preferences created using the XML File
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_preferences);
            new SettingsActivity().bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
            new SettingsActivity().bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_temperature_key)));
        }
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)e
     */
    protected void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for the preference changes
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener Immediately with preference's Current Value
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String stringValue = newValue.toString();

        if(preference instanceof ListPreference)
        {
            // For list preferences, look up the correct display value in the preference's entries list (since they have separate labels/values).

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if(prefIndex >= 0)
            {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }

        else
        {
            preference.setSummary(stringValue);
        }

        return true;
    }
}
