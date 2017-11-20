package com.example.dalibor.katalog.remote;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.dalibor.katalog.model.NotificationOpenedHandler;
import com.onesignal.OneSignal;

/**
 * Created by Employee on 17.11.2017..
 */

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor  = preferences.edit();
        editor.clear();
        editor.commit();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(getApplicationContext()))
                .init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }
}
