package com.example.dalibor.katalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class SelectedArtistActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button deleteArtist;
    private TextView lvArtistItemText;
    private Button editArtist;
    ApiService apiServiceDelete;
    private static final String TAG = "SelectedArtistActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_artist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvArtistItemText = (TextView) findViewById(R.id.tvArtistsGet);
        deleteArtist = findViewById(R.id.delArtistBtn);
        editArtist = findViewById(R.id.editArtistBtn);
        Intent intent = getIntent();
        String lvText = intent.getStringExtra("lvArtistText");
        lvArtistItemText.setText(lvText);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pom = preferences.getString("at","");
        apiServiceDelete = ApiUtils.getApiService(pom);


        editArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedArtistActivity.this, EditArtistActivity.class);
                String s = lvArtistItemText.getText().toString();
                intent.putExtra("Idstringtekst",s);
                startActivity(intent);
            }
        });

        deleteArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedArtistActivity.this);
                builder.setMessage("Da li ste sigurni?")
                        .setTitle("Fire missiles");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String ida = lvArtistItemText.getText().toString();
                        ida = (String) ida.subSequence(0, ida.indexOf(" "));
                        int pom = Integer.parseInt(ida);
                        deleteArtists(pom);
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

    public void deleteArtists(int id) {

        apiServiceDelete.deleteArtists(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "delete submitted to API." + response.body());
                    Intent intent = new Intent(SelectedArtistActivity.this,ArtistsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Uspesno",Toast.LENGTH_SHORT ).show();
                }
                else if(response.code()==401){
                    refreshToken();
                }
                else {
                    Log.e(TAG, "delete not submitted to API. - " + response.code() + " " + response.message());
                    Intent intent = new Intent(SelectedArtistActivity.this,ArtistsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code()+ " " + response.message(),Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(SelectedArtistActivity.this,ArtistsActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedArtistActivity.this);
                builder.setMessage("Da li ste sigurni da zelite da se izlogujete?")
                        .setTitle("Fire missiles");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
