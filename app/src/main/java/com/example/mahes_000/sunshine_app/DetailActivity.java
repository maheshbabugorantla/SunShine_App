package com.example.mahes_000.sunshine_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;

public class DetailActivity extends AppCompatActivity
{
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportFragmentManager().beginTransaction().add(R.id.container,new DetailActivityFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        getMenuInflater().inflate(R.menu.detailfragment, menu); // Adding the Share Weather Data Button to the App

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.share_weather);

        // Fetch and Store the ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an Intent to this ShareActionProvider, You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(new DetailActivityFragment().createShareForecastIntent());
        }

        else
        {
            Log.i(LOG_TAG, "Share Action Provider is null?");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
