package com.example.dalibor.katalog.remote;

import android.app.Application;

import com.onesignal.OneSignal;

/**
 * Created by Employee on 17.11.2017..
 */

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
