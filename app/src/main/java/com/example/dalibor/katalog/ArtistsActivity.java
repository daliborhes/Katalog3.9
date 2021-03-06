package com.example.dalibor.katalog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.model.Artists;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button addArtistsBtn;
    private ListView lvArtists;
    private ArrayAdapter<String> adapter;
    private ArrayList<Artists> listArtists;
    private ApiService apiServiceGet;
    private static final String TAG = "ArtistActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isOnline() == false) {

            Toast.makeText(ArtistsActivity.this, "Proverite internet konekciju", Toast.LENGTH_SHORT).show();
        }
        else {
            addArtistsBtn = (Button) findViewById(R.id.addArtistBtn);
            lvArtists = (ListView) findViewById(R.id.lvArtists);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String pom = preferences.getString("at", "");
            apiServiceGet = ApiUtils.getApiService(pom);

            getArtists();


            addArtistsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentaddArtist = new Intent(ArtistsActivity.this, AddArtistsActivity.class);
                    startActivity(intentaddArtist);
                }
            });

            lvArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int positionArtist, long l) {
                    Intent intentlvArtists = new Intent(ArtistsActivity.this, SelectedArtistActivity.class);
                    intentlvArtists.putExtra("lvArtistText", adapter.getItem(positionArtist));
                    startActivity(intentlvArtists);
                }
            });
        }



    }

    public void getArtists (){
        apiServiceGet.getArtists().enqueue(new Callback<ArrayList<Artists>>() {
            @Override
            public void onResponse(Call<ArrayList<Artists>> call, Response<ArrayList<Artists>> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "get submitted to API." + response.code() + " " + response.message());
                    listArtists = response.body();
                    for (int i = 0; i < listArtists.size(); i++){
                        adapter.add(listArtists.get(i).getArtistId() + " " + listArtists.get(i).getArtistName());
                    }

                    lvArtists.setAdapter(adapter);
                }
                else if (response.code()==401){

                    refreshToken();

                }
                else {
                    Log.e(TAG, "get not submitted to API. - " + response.code() + " " + response.message());
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                }

            }
            @Override
            public void onFailure(Call<ArrayList<Artists>> call, Throwable t) {

                Log.e(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(),"Neuspeno " + t.getMessage(),Toast.LENGTH_SHORT ).show();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ArtistsActivity.this);
                builder.setMessage("Da li ste sigurni da zelite da se izlogujete?")
                        .setTitle("Fire missiles");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
