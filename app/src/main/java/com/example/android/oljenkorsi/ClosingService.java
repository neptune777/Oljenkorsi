package com.example.android.oljenkorsi;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class ClosingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ClosingService","ClosingService.onTaskRemoved");
        return null;
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("ClosingService","ClosingService.onTaskRemoved");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getResources().getString(R.string.message_sending_status));
        editor.remove(getResources().getString(R.string.last_sent_location_accuracy_shared_preference_key));
        editor.commit();
        super.onTaskRemoved(rootIntent);

        // Destroy the service
        stopSelf();
    }
}
