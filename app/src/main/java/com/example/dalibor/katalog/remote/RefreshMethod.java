package com.example.dalibor.katalog.remote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.dalibor.katalog.LoginActivity;
import com.example.dalibor.katalog.model.AccessToken;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Employee on 16.11.2017..
 */

public class RefreshMethod {
    private Boolean ponovo;
    private static ApiService  apiServiceRefresh;

    public static void refreshToken (final String TAG, final Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String rT = preferences.getString("rt","");
        String text = "grant_type=refresh_token&refresh_token="+rT;
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), text);
        apiServiceRefresh.getAccesToken(body).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()){
                    AccessToken accessToken = response.body();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("at", accessToken.getAccessToken());
                    editor.putString("rt", accessToken.getRefreshToken());
                    editor.commit();
                    //ponovo = true;

                }

                if(response.code()==400){
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(context,"Sesija je istekla" + response.code()+response.message(),Toast.LENGTH_SHORT ).show();

                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "get not submitted to API: " + t.getMessage());
                Toast.makeText(context,"Neuspesno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });

    }
}
