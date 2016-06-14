package com.example.mahes_000.sunshine_app;



import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mahes_000.sunshine_app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    public static String mForecastStr;

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0; // This is used to link the Cursor Loader

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;


    public DetailActivityFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The Detail Activity called via intent. Inspect the intent for forecast data
    /*    Intent intent = getActivity().getIntent();

        if(intent != null)
        {
            mForecastStr = intent.getDataString();
        }

        if(mForecastStr != null)
        {
            ((TextView) rootView.findViewById(R.id.text_detail)).setText(mForecastStr);
        }*/

        return(rootView);
    }

    public Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); // Here if the API is less than 21 then we might have to use FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Log.v(LOG_TAG,"In onCreateLoader");

        Intent intent = getActivity().getIntent();

        if(intent == null)
        {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // Creating a Cursor for the data being displayed.

        Log.e(LOG_TAG,intent.getData().toString());

        return new CursorLoader(getActivity(),intent.getData(),FORECAST_COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.v(LOG_TAG, "In onLoadFinished");

        // Which means that there is no data returned by the Cursor
        if(!data.moveToFirst())
        {
            return;
        }

        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String maximumTemperature = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String minimumTemperature = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        mForecastStr = String.format("%s - %s - %s / %s", dateString, weatherDescription, maximumTemperature, minimumTemperature);
        Log.i(LOG_TAG, mForecastStr);

        TextView detailView = (TextView) getView().findViewById(R.id.text_detail);
        detailView.setText(mForecastStr);

        if(new DetailActivity().mShareActionProvider != null)
        {
            new DetailActivity().mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);

        super.onActivityCreated(savedInstanceState);
    }

 }
