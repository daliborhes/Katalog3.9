package com.example.dalibor.katalog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button artistsBtn;
    private Button songsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistsBtn = (Button) findViewById(R.id.artistsBtn);
        songsBtn = (Button) findViewById(R.id.songsBtn);

        artistsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArtists = new Intent(MainActivity.this, ArtistsActivity.class);
                startActivity(intentArtists);
            }
        });

        songsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSongs = new Intent(MainActivity.this, SongsActivity.class);
                startActivity(intentSongs);
            }
        });
    }
}
