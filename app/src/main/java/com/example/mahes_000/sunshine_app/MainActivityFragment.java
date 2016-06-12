package com.example.mahes_000.sunshine_app;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.json.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{

    //MenuItem menuItem;

    String[] forecastArray = {"Today-Sunny-88/63", "Tomorrow-Foggy-70/46", "Weds-Cloudy-77/63", "Thurs-Rainy-45/66", "Fri-Foggy-70/46", "Sat-Sunny-76/68"};

    String[] forecastedWeather = null;
    ArrayAdapter<String> stringArrayAdapter;

//    private ShareActionProvider mShareActionProvider;
    String Zipcode = "";

    public MainActivityFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Zipcode = preference.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        getForecast(Zipcode);
    }

    // This is used to create the Options in the Menu Bar such as Settings, Refresh_Data, etc
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecastfragment, menu); // Adding the Refresh Option to Menu
        inflater.inflate(R.menu.get_location,menu); // Adding Show Map Location Option to the Menu

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Zipcode = preference.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
            getForecast(Zipcode);
        }

        else if (id == R.id.show_location)
        {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This function is used to show the Location of the ZipCode entered in the Settings on the World Map.
    // This function makes use of the apps that are already installed on the phone to show the ZipCode Location on the world map
    private void openPreferredLocationInMap()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // This is used to get the User set location
        String Location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Intent intent = new Intent(Intent.ACTION_VIEW);

        // This Uri is used to make a query to the apps that provide access to maps on the phone using the location information obtained from 'Location' variable
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",Location).build();
        intent.setData(geoLocation);

        // This starts the activity only when there is an app to show the GeoLocation on the Maps. for E.g Google Maps.
        if(intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> forecastData = new ArrayList<>(Arrays.asList(forecastArray));

        stringArrayAdapter =  new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,forecastData);

        RelativeLayout Container = (RelativeLayout) rootView.findViewById(R.id.Relative_Layout);

        // This is used for list the weather for a week
        final ListView listView = (ListView) Container.findViewById(R.id.listview_forcast);

        listView.setAdapter(stringArrayAdapter); // This sets all the strings in forecastArray to be in listView

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = stringArrayAdapter.getItem(position);

                Intent toDetailActivity = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);

                startActivity(toDetailActivity);
            }
        });

        getForecast(Zipcode);

        return(rootView);
    }

    private boolean getForecast(String ZipCode)
    {
        try
        {
            // Used to manage the Active Data Network Connections
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null)
            {
                DownloadTask downloadTask = new DownloadTask();

                try
                {
                    downloadTask.execute(ZipCode).get();
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                Toast.makeText(getActivity(), "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;

    }

    public class DownloadTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... urls) {

            String[] weather_forecast = getData(urls[0]);

            return weather_forecast;
        }

        public String[] getData(String postalCode) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
            final String QUERY_PARAMETER = "q";
            final String MODE_PARAMETER = "mode";
            final String UNITS_PARAMETER = "units";
            final String DAYS_PARAM = "cnt";
            final String ID = "APPID";
            final String API_Key = "486563a380c9eb938fdb890668697f20";
            String format = "json";
            String days = "7";
            String units = "metric";

            Uri BuiltUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAMETER, postalCode + ",US").appendQueryParameter(ID,API_Key).appendQueryParameter(MODE_PARAMETER, format).appendQueryParameter(UNITS_PARAMETER, units).appendQueryParameter(DAYS_PARAM,days).build();

            String urlString = BuiltUri.toString();

            Log.i("Final URL: ", urlString);

            try {

                URL url = new URL(urlString);

                // Creates a request to OpenWeatherMap, and open the Connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                String forecastJsonStr = null; // This contains the Raw JSON Data in String Format

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    // Since its JSON adding a newline character won't have any affect on Parsing. But newline character is added to make the Debugging Easier
                    buffer.append(line + "\n");
                }

                // Just a Sanity Check
                if (buffer.length() == 0) {
                    return null;
                }

                forecastJsonStr = buffer.toString();

                System.out.println("Weather Info: " + forecastJsonStr);

                forecastedWeather = getWeatherDataFromJson(forecastJsonStr,Integer.parseInt(days));

            }

            catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Exception: ", "Malformed URLException Occurred");
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.e("Exception: ", "Protocol Exception Occurred");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception: ", "IO Exception Occurred");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: " + e.getMessage());
                Log.e("Exception: ", "Unknown Exception Occurred");
            } finally {
                // Checking to see if the UrlConnection is opened and closing it if opened
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    }

                    // This is thrown when there is no Input Stream given out by the InputStreamReader
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Exception: ", "IO Exception closing the reader Occurred");
                    }
                }
            }

            return forecastedWeather;
        }

        @Override
        protected void onPostExecute(String[] s)
        {
            super.onPostExecute(s);

            stringArrayAdapter.clear(); // This clears the previously held forecast

            for(String str: s)
            {
                stringArrayAdapter.add(str);
            }

            return;
        }
    }

    private String getReadableDateString(long time)
    {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return(shortenedDateFormat.format(time));
    }

    private String formatHighLows(double high, double low)
    {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String Units;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Units = preferences.getString(getString(R.string.pref_temperature_key),getString(R.string.pref_temperature_default));

        if(Units.equals("Fahrenheit"))
        {
            roundedHigh = Math.round(roundedHigh * 1.8 + 32);
            roundedLow = Math.round(roundedLow * 1.8 + 32);
        }

        String highLowStr = roundedHigh + "/" + roundedLow; // Here the values of Temperature are by default in Celsius

        return(highLowStr);
    }

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException
    {
        // These are the names of the JSON Objects that are needed to be extracted
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESC = "main";

        JSONObject rootObj = new JSONObject(forecastJsonStr);

        JSONArray list_Array = rootObj.getJSONArray(OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow(); // This sets the time of the given Time object to the Current time.

        // Here we are starting at the day returned by the local time.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(),dayTime.gmtoff);

        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int index = 0; index < list_Array.length(); index++)
        {
            String day;
            String description;
            String highandLow;

            // Fetching the JSON Object for each day
            JSONObject list_obj = list_Array.getJSONObject(index);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay + index);
            day = getReadableDateString(dateTime);

            JSONArray weather_array = list_obj.getJSONArray(OWM_WEATHER);
            JSONObject weather_obj = weather_array.getJSONObject(0);
            description = weather_obj.getString(OWM_DESC);

            JSONObject temp_obj = list_obj.getJSONObject(OWM_TEMPERATURE);

            Double max_temp = temp_obj.getDouble(OWM_MAX);
            Double min_temp = temp_obj.getDouble(OWM_MIN);

            highandLow = formatHighLows(max_temp, min_temp);

            resultStrs[index] = day + " - " + description + " - " + highandLow;

        }

        for(String str: resultStrs)
        {
            Log.i("Forecast entry: ", str );
        }

        return resultStrs;
    }
}