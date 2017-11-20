package com.example.dalibor.katalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedSongActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button deleteSong;
    private Button editSong;
    private TextView lvSongItemTxt;
    ApiService apiServiceDelete;
    private static final String TAG = "SelectedSongActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_song);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        deleteSong = (Button) findViewById(R.id.delSongsBtn);
        editSong = findViewById(R.id.editSongsBtn);
        lvSongItemTxt = (TextView) findViewById(R.id.tvSongGet);
        Intent intent = getIntent();
        String lvText = intent.getStringExtra("lvSongText");
        lvSongItemTxt.setText(lvText);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pom = preferences.getString("at","");
        apiServiceDelete = ApiUtils.getApiService(pom);

        editSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedSongActivity.this, EditSongActivity.class);
                String s = lvSongItemTxt.getText().toString();
                intent.putExtra("Idstringtekst",s);
                startActivity(intent);
            }
        });

        deleteSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedSongActivity.this);
                builder.setMessage("Da li ste sigurni?")
                        .setTitle("Fire missiles");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String ids = lvSongItemTxt.getText().toString();
                        ids = (String) ids.subSequence(0, ids.indexOf(" "));
                        int pom = Integer.parseInt(ids);
                        deleteSongs(pom);
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
        });
    }

    public void deleteSongs(int id) {

        apiServiceDelete.deleteSongs(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "delete submitted to API." + response.body());
                    Intent intent = new Intent(SelectedSongActivity.this,SongsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Uspesno",Toast.LENGTH_SHORT ).show();
                }
                else if(response.code()==401){
                    refreshToken();
                }
                else {
                    Log.e(TAG, "delete not submitted to API. - " + response.code() + " " + response.message());
                    Intent intent = new Intent(SelectedSongActivity.this,SongsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code()+ " " + response.message(),Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(SelectedSongActivity.this,SongsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Neuspesno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();
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
