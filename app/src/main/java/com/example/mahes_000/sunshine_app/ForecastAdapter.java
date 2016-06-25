package com.example.mahes_000.sunshine_app;

/**
 * Created by mahes_000 on 6/11/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahes_000.sunshine_app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter
{

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean setTodayView = true;

    public ForecastAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1; // Which means that no layout available
        View view = null;

        if(viewType == VIEW_TYPE_TODAY)
        {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast_today, parent, false);
        }

        else if(viewType == VIEW_TYPE_FUTURE_DAY)
        {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        }

        // Here using ViewHolder Pattern we can the ListView Scroll smoother
        // because we need to constantly keep calling findViewById method whenever a View is recycled.

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public void setTodayViewType(boolean todayView)
    {
        setTodayView = todayView;
    }

    @Override
    public int getItemViewType(int position)
    {
        return (position == 0 && setTodayView) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    // Here we are returning two because we are using two different view types (One for Today and other type for future days).
    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Setting the date
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,dateInMillis));

        // Setting the Forecast Description of the day
        // This is the short description for the weather of the day
        String weather_desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(weather_desc);

        // Setting the Images Icons that suit the Weather Description
        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY)
        {
            int status = Utility.getArtResourceforWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));

            if(status != -1)
            {
                viewHolder.iconView.setImageResource(status);
            }

            else
            {
                viewHolder.iconView.setImageResource(R.drawable.clear_sky);
            }
        }

        else
        {
            int status = Utility.getIconResourceforWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));

            if(status != -1)
            {
                viewHolder.iconView.setImageResource(status);
            }

            else
            {
                viewHolder.iconView.setImageResource(R.drawable.clear_sky);
            }

//            viewHolder.iconView.setImageResource(Utility.getArtResourceforWeatherCondition(ForecastFragment.COL_WEATHER_CONDITION_ID));
        }

        // Setting the Temperature
        boolean isMetric = Utility.isMetric(context);

        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        // Setting the Maximum Temperature
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Setting the Minimum Temperature
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }


    public static class ViewHolder
    {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view)
        {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}