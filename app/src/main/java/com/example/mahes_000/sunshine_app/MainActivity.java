package com.example.mahes_000.sunshine_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback
{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    protected static final String DETAILFRAGMENT_TAG = "DFTAG";

    public String mLocation;
    public boolean mTwoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) != null)
        {
            // The detail container view will only be present in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity then the activity
            // should be in the two-pane mode
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new DetailActivityFragment(),DETAILFRAGMENT_TAG).commit();
            }
        }

        else
        {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String location = Utility.getPreferredLocation(this); // New Location.

        if(location != null && !location.equals(new MainActivity().mLocation))
        {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

            if (ff != null)
            {
                ff.onLocationChanged();
            }
        }

        DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

        if(detailActivityFragment != null)
        {
            detailActivityFragment.onLocationChanged(location);
        }

        mLocation = location;
    }

    @Override
    public void onItemSelected(Uri dateUri)
    {
        if(mTwoPane)
        {
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, dateUri);

            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            // Here we are replacing the existing fragment in the MainActivity with a new fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, detailActivityFragment, DETAILFRAGMENT_TAG).commit();
        }

        // This works for screen sizes less than 600dp
        else
        {
            Log.d(LOG_TAG, " : Inside Phone Screen");
            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(intent);
        }
    }
}