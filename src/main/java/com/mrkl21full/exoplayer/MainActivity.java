package com.mrkl21full.exoplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mrkl21full.exoplayer.media.RadioPlayerActivity;
import com.mrkl21full.exoplayer.media.VideoPlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.radioButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RadioPlayerActivity.class)));
        findViewById(R.id.customVideoButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, VideoPlayerActivity.class)));
    }
}
