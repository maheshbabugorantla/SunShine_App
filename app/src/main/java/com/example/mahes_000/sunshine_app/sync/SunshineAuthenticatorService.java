package com.example.mahes_000.sunshine_app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by mahes_000 on 6/26/2016.
 */
public class SunshineAuthenticatorService extends Service
{
    private SunshineAuthenticator mAuthenticator;

    @Override
    public void onCreate()
    {
        mAuthenticator = new SunshineAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mAuthenticator.getIBinder();
    }
}
