package com.example.mahes_000.sunshine_app;

/**
 * Created by mahes_000 on 6/11/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility
{
    public static final String DATE_FORMAT = "yyyyMMdd";

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_temperature_key),
                context.getString(R.string.pref_temperature_default))
                .equals(context.getString(R.string.pref_temperature_default));
    }

    static String formatTemperature(Context context, double temperature, boolean isMetric)
    {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    static String formatDate(long dateInMillis)
    {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    public static String getFriendlyDayString(Context context, long dateinMillis)
    {
        // The day string for forecast uses the following logic:
        // For Today: "Today, June 8"
        // For Tomorrow: "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateinMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime,time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is 'Today, June 24

        if(julianDay == currentJulianDay)
        {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;

            return String.format(context.getString(formatId,today, getFormattedMonthDay(context,dateinMillis)));
        }

        else if (julianDay < currentJulianDay + 7)
        {
            return getDayName(context, dateinMillis);
        }

        else
        {
            SimpleDateFormat shortenedDateFormat =new SimpleDateFormat("EEE MMM dd ");
            return shortenedDateFormat.format(dateinMillis);
        }
    }

    public static String getDayName(Context context, long dateInMillis)
    {

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

        if(julianDay == currentJulianDay)
        {
            return context.getString(R.string.today);
        }
        else if(julianDay == currentJulianDay + 7)
        {
            return context.getString(R.string.tomorrow);
        }
        else
        {
            Time time = new Time();
            time.setToNow();

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getFormattedMonthDay(Context context, long dateInMillis)
    {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static int getIconResourceforWeatherCondition(int WeatherId)
    {
        if((WeatherId >= 200) && (WeatherId <= 232))
        {
            return(R.drawable.ic_storm);
        }
        else if((WeatherId >= 300) && (WeatherId <= 321))
        {
            return(R.drawable.ic_light_rain);
        }
        else if((WeatherId >= 500) && (WeatherId <= 504))
        {
            return(R.drawable.ic_rain);
        }
        else if(WeatherId == 511)
        {
            return(R.drawable.ic_snow);
        }
        else if((WeatherId >= 520) && (WeatherId <= 531))
        {
            return(R.drawable.ic_rain);
        }
        else if((WeatherId >= 600) && (WeatherId <= 622))
        {
            return(R.drawable.ic_snow);
        }
        else if((WeatherId >= 701) && (WeatherId <= 761))
        {
            return(R.drawable.ic_fog);
        }
        else if((WeatherId >= 762) && (WeatherId <= 781))
        {
            return(R.drawable.ic_storm);
        }
        else if(WeatherId == 800)
        {
            return(R.drawable.ic_clear);
        }
        else if((WeatherId == 801))
        {
            return(R.drawable.ic_light_clouds);
        }
        else if((WeatherId >= 802) && (WeatherId <= 804))
        {
            return(R.drawable.ic_cloudy);
        }


        Log.i("Utility Icon Source: ", Integer.toString(WeatherId));
        return(-1);
    }

    public static int getArtResourceforWeatherCondition(int WeatherId)
    {

        if((WeatherId >= 200) && (WeatherId <= 232))
        {
            return(R.drawable.art_storm);
        }
        else if((WeatherId >= 300) && (WeatherId <= 321))
        {
            return(R.drawable.art_light_rain);
        }
        else if((WeatherId >= 500) && (WeatherId <= 504))
        {
            return(R.drawable.art_rain);
        }
        else if(WeatherId == 511)
        {
            return(R.drawable.art_snow);
        }
        else if((WeatherId >= 520) && (WeatherId <= 531))
        {
            return(R.drawable.art_rain);
        }
        else if((WeatherId >= 600) && (WeatherId <= 622))
        {
            return(R.drawable.art_snow);
        }
        else if((WeatherId >= 701) && (WeatherId <= 761))
        {
            return(R.drawable.art_fog);
        }
        else if((WeatherId >= 762) && (WeatherId <= 781))
        {
            return(R.drawable.art_storm);
        }
        else if(WeatherId == 800)
        {
            return(R.drawable.art_clear);
        }
        else if((WeatherId == 801))
        {
            return(R.drawable.art_light_clouds);
        }
        else if((WeatherId >= 802) && (WeatherId <= 804))
        {
            return(R.drawable.art_clouds);
        }

        Log.i("Utility Art Source: ", Integer.toString(WeatherId));
        return(-1); // Which means that none of the above condition is met.
    }
}