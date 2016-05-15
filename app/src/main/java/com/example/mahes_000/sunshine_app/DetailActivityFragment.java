package com.example.mahes_000.sunshine_app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailActivityFragment extends Fragment
{

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    public String mForecastStr;
    public DetailActivityFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if ((intent != null) && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mForecastStr = new String(intent.getStringExtra(Intent.EXTRA_TEXT));
            //intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.i("Forecast ", mForecastStr);
            ((TextView) rootView.findViewById(R.id.text_detail)).setText(mForecastStr);
        }
        return rootView;
    }

    public Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); // Here if the API is less than 21 then we might have to use FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }
}
