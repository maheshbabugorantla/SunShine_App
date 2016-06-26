package com.example.mahes_000.sunshine_app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by mahes_000 on 6/26/2016.
 */
public class SunshineSyncService extends Service
{
    private static final Object sSyncAdapterLock = new Object();
    private static SunshineSyncAdapter sSunShineSyncAdapter = null;

    private static final String LOG_TAG = SunshineSyncService.class.getSimpleName();

    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "onCreate - SunshineSyncCrate");
        synchronized (sSyncAdapterLock)
        {
            if(sSunShineSyncAdapter == null)
            {
                sSunShineSyncAdapter = new SunshineSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunShineSyncAdapter.getSyncAdapterBinder();
    }
}
