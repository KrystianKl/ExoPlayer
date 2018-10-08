package com.krystiankl.a14_exoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.krystiankl.a14_exoplayer.media.RadioPlayerActivity;
import com.krystiankl.a14_exoplayer.media.VideoPlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.radioButton).setOnClickListener(v -> {
            if(checkInternet(getApplicationContext()))
                startActivity(new Intent(MainActivity.this, RadioPlayerActivity.class));
            else
                Toast.makeText(getApplicationContext(), getString(R.string.app_nointernet), Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.customVideoButton).setOnClickListener(v -> {
            if(checkInternet(getApplicationContext()))
                startActivity(new Intent(MainActivity.this, VideoPlayerActivity.class));
            else
                Toast.makeText(getApplicationContext(), getString(R.string.app_nointernet), Toast.LENGTH_LONG).show();
        });
    }

    public static boolean checkInternet(Context c) {
        if (c == null) return false;
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (conManager.getActiveNetworkInfo() != null && conManager.getActiveNetworkInfo().isConnected());
    }
}
