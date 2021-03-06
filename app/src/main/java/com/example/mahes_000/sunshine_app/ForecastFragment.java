package com.example.mahes_000.sunshine_app;

/**
 * Created by mahes_000 on 6/11/2016.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mahes_000.sunshine_app.data.WeatherContract;
import com.example.mahes_000.sunshine_app.sync.SunshineSyncAdapter;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ForecastAdapter mForecastAdapter;
    private ListView mListView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int FORECAST_LOADER = 0; // Loader ID
    private boolean mUseTodayView = false;


    //private String FORECASTFRAGMENT_TAG = "FFTAG";

    private static final String[] FORECAST_COLUMNS =
            {

            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
        };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public interface Callback
    {
        void onItemSelected(Uri dateUri);
    }

    public ForecastFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        // This setting will give us the forecast from the present date to the desired no.of days in future.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        String locationSetting = Utility.getPreferredLocation(getActivity());

        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting,System.currentTimeMillis());

        return new CursorLoader(getActivity(), weatherUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mForecastAdapter.swapCursor(data);

        int mPosition = new MainActivity().getItemPosition();

        if(mPosition != ListView.INVALID_POSITION)
        {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecastfragment, menu);
        inflater.inflate(R.menu.get_location, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            updateWeather();
            return true;
        }

        else if(id == R.id.show_location)
        {
            openPreferredMapLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);
        mForecastAdapter.setTodayViewType(mUseTodayView);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forcast);
        mListView.setAdapter(mForecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null)
                {
                    String locationSetting = Utility.getPreferredLocation(getActivity());

                    // This is used for normal screens whose smallest width is lesser than 600dp
                    ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,cursor.getLong(COL_WEATHER_DATE)));
                }

                new MainActivity().setItemPosition(position);
            }
        });


        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            new MainActivity().setItemPosition(savedInstanceState.getInt(SELECTED_KEY));
            Log.d("Inside onCreateView: ", "Position restored to " + Integer.toString(new MainActivity().getItemPosition()));
        }

        Log.d("Inside onCreateView: ", "Position restored to " + Integer.toString(new MainActivity().getItemPosition()));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    public void updateWeather()
    {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String location = Utility.getPreferredLocation(getActivity()); // New Location.

        if(location != null && !location.equals(new MainActivity().getItemPosition()))
        {
           // ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            onLocationChanged();
            new MainActivity().setItemPosition(Integer.parseInt(location));
        }
    }

    protected void onLocationChanged()
    {
        updateWeather(); // Updating the Weather
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void openPreferredMapLocation()
    {
        String openLocation = Utility.getPreferredLocation(getActivity());

        Intent intent = new Intent(Intent.ACTION_VIEW);

        // This Uri is used to make a query to the apps that provide access to maps on the phone using the location information obtained from 'Location' variable
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",openLocation).build();
        intent.setData(geoLocation);

        // This starts the activity only when there is an app to show the GeoLocation on the Maps. for E.g Google Maps.
        if(intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    public void setTodayView(boolean todayView)
    {
        mUseTodayView = todayView;

        // The reason we are check for the null is because this a public method and in Future it might be called before onCreateView.
        if(mForecastAdapter != null)
        {
            mForecastAdapter.setTodayViewType(mUseTodayView);
        }

        return;
    }

}