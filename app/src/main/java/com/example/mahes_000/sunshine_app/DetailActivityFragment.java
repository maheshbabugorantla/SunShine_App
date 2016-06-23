package com.example.mahes_000.sunshine_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahes_000.sunshine_app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    public static String mForecastStr;

    private Uri mUri = null;

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0; // This is used to link the Cursor Loader

    static final String DETAIL_URI = "URI";

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WINDSPEED = 6;
    private static final int COL_WEATHER_PRESSURE = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_ICON = 9;

    public DetailActivityFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();

        if(arguments != null)
        {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        if (mUri != null)
        {
            return new CursorLoader(getActivity(), mUri, FORECAST_COLUMNS, null, null, null);
        }

        return null;
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

        // For more Details on the default units refer to
        // http://openweathermap.org/current and check for "Weather parameters in API respond" heading on the WebPage

//        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String dateString = Utility.getFriendlyDayString(getActivity(),data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String maximumTemperature = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String minimumTemperature = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        String atmos_pressure = Double.toString(data.getDouble(COL_WEATHER_PRESSURE));
        String atmos_humidity = Double.toString(data.getDouble(COL_WEATHER_HUMIDITY));
        double wind_speed = data.getDouble(COL_WEATHER_WINDSPEED);
        double wind_direction = data.getDouble(COL_WEATHER_DEGREES);

        mForecastStr = String.format("%s - %s - %s / %s", dateString, weatherDescription, maximumTemperature, minimumTemperature);
        Log.i(LOG_TAG, mForecastStr);

        // Setting the Date
        TextView dateView = (TextView) getView().findViewById(R.id.column_date_textView);
        dateView.setText(dateString);

        // Setting the Weather Description
        TextView weather_desc_View = (TextView) getView().findViewById(R.id.weather_desc_textView);
        weather_desc_View.setText(weatherDescription);

        // Setting the Maximum Temperature
        TextView weather_max_temp = (TextView) getView().findViewById(R.id.column_max_temp_textView);
        weather_max_temp.setText(maximumTemperature);

        // Setting the Minimum Temperature
        TextView weather_min_temp = (TextView) getView().findViewById(R.id.column_min_temp_textView);
        weather_min_temp.setText(minimumTemperature);

        // Setting the Image Source based on the weather
        ImageView weather_icon = (ImageView) getView().findViewById(R.id.weather_icon);
        weather_icon.setImageResource(Utility.getArtResourceforWeatherCondition(data.getInt(COL_WEATHER_ICON)));

        // Atmospheric Pressure default units are in hPa
        TextView weather_Pressure = (TextView) getView().findViewById(R.id.pressure_textView);
        weather_Pressure.setText(atmos_pressure + " hPa");

        // Atmospheric Humidity. Default Units: in Percentage (%)
        TextView weather_Humidity = (TextView) getView().findViewById(R.id.humidity_textView);
        weather_Humidity.setText(atmos_humidity + " %");

        // Wind Speed and Direction. Default Units: Speed => meters/second && Direction => Degrees
        TextView weather_Wind = (TextView) getView().findViewById(R.id.wind_textView);
        wind_speed = wind_speed * 3.6; // Converting to km/h (kmph)

        String direction = "NE";

        if((wind_direction >= 0)  && (wind_direction <= 90))
        {
            direction = "NE";
        }

        else if((wind_direction <= 180)  && (wind_direction > 90))
        {
            direction = "NW";
        }

        else if((wind_direction <= 270) && (wind_direction > 180))
        {
            direction = "SW";
        }

        else if((wind_direction <= 360) && (wind_direction > 270))
        {
            direction = "SE";
        }

        weather_Wind.setText(String.format("%.2f", wind_speed) + " Km/h " + direction);
//        weather_Wind.setText(Double.toString(wind_speed) + " Km/h " + direction);

        Log.i(LOG_TAG, "Pressure: " + atmos_pressure);
        Log.i(LOG_TAG, "Humidity: " + atmos_humidity);
        Log.i(LOG_TAG, "Wind: " + wind_speed + " Wind Direction: " + wind_direction);

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
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged(String newLocation)
    {
        Uri uri = mUri;
        if(null != uri)
        {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }

 }
