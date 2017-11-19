package com.example.dalibor.katalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSongsActivity extends AppCompatActivity {

    private Button confirmAddSong;
    private EditText editAddSong;
    private ApiService apiServicePost;
    private static final String TAG = "AddSongsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);
        confirmAddSong = (Button) findViewById(R.id.confirmSong);
        editAddSong = (EditText) findViewById(R.id.addsong_edittext);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pom = preferences.getString("at","");
        apiServicePost = ApiUtils.getApiService(pom);


        confirmAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(editAddSong.getText().toString().trim())
                    //editAddArtist.getText().toString().equals("null") || editAddArtist.getText().toString().equals("")
                        )

                {
                    Toast.makeText(getApplicationContext(),"Unesite ime",Toast.LENGTH_SHORT ).show();
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddSongsActivity.this);
                    builder.setMessage("Da li ste sigurni?")
                            .setTitle("Fire missiles");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String name = editAddSong.getText().toString().trim();
                            postSongs(name);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

    }

    public void postSongs(String name){

        apiServicePost.postSongs(name).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "post submitted to API. " + response.code() + " " + response.message());
                    Intent intent = new Intent(AddSongsActivity.this,SongsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Uspesno",Toast.LENGTH_SHORT ).show();
                }
                else if (response.code()==401){
                    refreshToken();
                }
                else {
                    Log.e(TAG, "post not submitted to API. - " + response.code() + " " + response.message());
                    Intent intent = new Intent(AddSongsActivity.this,SongsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(AddSongsActivity.this,SongsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Neuspeno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();

            }

        });
    }
    public void refreshToken (){
        ApiService apiServiceRefresh = ApiUtils.getApiService();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String rT = preferences.getString("rt","");
        String text = "grant_type=refresh_token&refresh_token="+rT;
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), text);
        apiServiceRefresh.getAccesToken(body).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()){
                    AccessToken accessToken = response.body();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("at", accessToken.getAccessToken());
                    editor.putString("rt", accessToken.getRefreshToken());
                    editor.commit();

                }

                else if(response.code()==400){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Sesija je istekla " + response.code()+response.message(),Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "get not submitted to API: " + t.getMessage());
                Toast.makeText(getApplicationContext(),"Neuspesno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });

    }

}