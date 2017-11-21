package com.example.dalibor.katalog.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.dalibor.katalog.LoginActivity;
import com.example.dalibor.katalog.SongsActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Employee on 21.11.2017..
 */

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler{

    private Context context;
    private String at = null;

    //    public ExampleNotificationOpenedHandler(Context context){
//        this.context=context;
//    }
    public NotificationOpenedHandler (Context context){
        this.at=at;
        this.context=context;
    }
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;



        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

//         The following can be used to open an Activity of your choice.
//         Replace - getApplicationContext() - with any Android Context.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String at = preferences.getString("at","");

        if (at == null || at.equals("")){
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else {
            Intent intent = new Intent(context, SongsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
//         Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
//           if you are calling startActivity above.

//        <application ...>
//          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
//        </application>
//
    }
}
