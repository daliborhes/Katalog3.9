package com.example.dalibor.katalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class EditArtistActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String s;
    private Button confirmEdit;
    private EditText editArtist;
    private ApiService apiServiceEdit;
    private static final String TAG = "EditArtistActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artist);

        confirmEdit = findViewById(R.id.confirmEditArtist);
        editArtist = findViewById(R.id.editartist_edittext);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s = getIntent().getStringExtra("Idstringtekst");
        editArtist.setHint(s.subSequence(s.indexOf(" ")+1,s.length()));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pom = preferences.getString("at","");
        apiServiceEdit = ApiUtils.getApiService(pom);
        confirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editArtist.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Unesite ime",Toast.LENGTH_SHORT ).show();
                }
                else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(EditArtistActivity.this);
                    builder.setMessage("Da li ste sigurni?")
                            .setTitle("Fire missiles");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            s = (String) s.subSequence(0, s.indexOf(" "));
                            int pom = Integer.parseInt(s);
                            String name = editArtist.getText().toString().trim();
                            editArtists(pom, name);
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

    public void OpenDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Da li ste sigurni?")
                .setTitle("Fire missiles");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void editArtists (int id, String name){

        apiServiceEdit.editArtists(id, name).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "edit submitted to API." + response.code() + " " + response.message());
                    Intent intent = new Intent(EditArtistActivity.this,ArtistsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Uspesno",Toast.LENGTH_SHORT ).show();
                }
                else if (response.code()==401){
                    refreshToken();
                }
                else {
                    Log.e(TAG, "edit not submitted to API. - " + response.code() + " " + response.message());
                    Intent intent = new Intent(EditArtistActivity.this,ArtistsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(EditArtistActivity.this,ArtistsActivity.class);
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
