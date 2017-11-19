package com.example.dalibor.katalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.model.Songs;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongsActivity extends AppCompatActivity {

    private Button addSongsBtn;
    private ListView lvSongs;
    private ArrayAdapter<String> adapter2;
    private ApiService apiServiceGet;
    private ArrayList<Songs> listSongs;
    private static final String TAG = "SongsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        addSongsBtn = (Button) findViewById(R.id.addSongsBtn);
        lvSongs = (ListView) findViewById(R.id.lvSongs);

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pom = preferences.getString("at","");
        apiServiceGet = ApiUtils.getApiService(pom);

        getSongs();


        addSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentaddSong = new Intent(SongsActivity.this, AddSongsActivity.class);
                startActivity(intentaddSong);
            }
        });

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positionSong, long l) {
                Intent intentlvSongs = new Intent(SongsActivity.this, SelectedSongActivity.class);
                intentlvSongs.putExtra("lvSongText", adapter2.getItem(positionSong));
                startActivity(intentlvSongs);
            }
        });



    }

    public void getSongs (){
        apiServiceGet.getSongs().enqueue(new Callback<ArrayList<Songs>>() {
            @Override
            public void onResponse(Call<ArrayList<Songs>> call, Response<ArrayList<Songs>> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "get submitted to API." + response.code() + " " + response.message());
                    listSongs = response.body();
                    for (int i = 0; i < listSongs.size(); i++){
                        adapter2.add(listSongs.get(i).getSongId() + " " + listSongs.get(i).getSongName());
                    }
                    lvSongs.setAdapter(adapter2);
                }
                else if (response.code()==401){
                    refreshToken();
                }
                else {
                    Log.e(TAG, "get not submitted to API. - " + response.code() + " " + response.message());
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code()+ " " + response.message(),Toast.LENGTH_SHORT ).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Songs>> call, Throwable t) {

                Log.e(TAG, t.getMessage());
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
