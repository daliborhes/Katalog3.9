package com.example.dalibor.katalog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splashScreen extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tv = findViewById(R.id.tvSplash);
        iv = findViewById(R.id.splashImg);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);
        final Intent intent = new Intent(this,LoginActivity.class);

        Thread splash = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        splash.start();
    }
}
