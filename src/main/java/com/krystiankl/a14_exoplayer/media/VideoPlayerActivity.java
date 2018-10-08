package com.krystiankl.a14_exoplayer.media;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.krystiankl.a14_exoplayer.R;

import java.util.HashMap;

public class VideoPlayerActivity extends Activity implements View.OnClickListener {

    private static final String KEY_MEDIA_PLAYER = "https://www.sadecemp3indir.mobi/uploads/mp3/ac9e64171b58a296b00829a88d7050c7.mp3";

    private static final String KEY_PLAY_WHEN_READY = "play_when_ready";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";

    private PlayerView playerView;
    private SimpleExoPlayer player;

    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private TrackGroupArray lastSeenTrackGroupArray;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;

    private ProgressBar progressBar;
    private ImageView ivHideControllerButton, ivRepeatControllerButton, ivSettings;
    private boolean playWhenReady;
    private int currentWindow, repeat = 0;
    private long playbackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        if (savedInstanceState == null) {
            playWhenReady = true;
            currentWindow = 0;
            playbackPosition = 0;
        } else {
            playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
            currentWindow = savedInstanceState.getInt(KEY_WINDOW);
            playbackPosition = savedInstanceState.getLong(KEY_POSITION);
        }

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener) bandwidthMeter);
        ivHideControllerButton = findViewById(R.id.exo_controller);
        ivRepeatControllerButton = findViewById(R.id.exo_repeat);
        ivSettings = findViewById(R.id.settings);
        progressBar = findViewById(R.id.progress_bar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializePlayer() {
        playerView = findViewById(R.id.player_view);
        playerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        lastSeenTrackGroupArray = null;

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        playerView.setPlayer(player);
        player.addListener(new PlayerEventListener());
        player.setPlayWhenReady(shouldAutoPlay);

        /* MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),mediaDataSourceFactory, mainHandler, null); */
        MediaSource mediaSource = new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(KEY_MEDIA_PLAYER));

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(KEY_MEDIA_PLAYER, new HashMap<>());
        String artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title[] = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).split("\\(");

        if(artist.length() >= 2 && title.length >= 1) {
            WebView webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://www.azlyrics.com/lyrics/" + artist.replaceAll("\\s+", "").toLowerCase() + "/" + title[0].toLowerCase().replaceAll("\\s+", "") + ".html");
        }

        boolean haveStartPosition = currentWindow != C.INDEX_UNSET;
        if (haveStartPosition) player.seekTo(currentWindow, playbackPosition);

        player.prepare(mediaSource, !haveStartPosition, false);
        updateButtonVisibilities();

        ivHideControllerButton.setOnClickListener(v -> playerView.hideController());
        ivRepeatControllerButton.setOnClickListener(v -> repeatOption());
    }

    private void repeatOption() {
        switch(repeat) {
            case 0:
                ivRepeatControllerButton.setImageResource(R.drawable.ic_repeat_one);
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
                repeat = 1;
                Toast.makeText(this, "Repeat mode - One", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                ivRepeatControllerButton.setImageResource(R.drawable.ic_repeat_all);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                repeat = 2;
                Toast.makeText(this, "Repeat mode - All", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                ivRepeatControllerButton.setImageResource(R.drawable.ic_repeat_none);
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
                Toast.makeText(this, "Repeat mode - None", Toast.LENGTH_SHORT).show();
                repeat = 0;
                break;
            default:
                ivRepeatControllerButton.setImageResource(R.drawable.ic_repeat_none);
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
                Toast.makeText(this, "Repeat mode - None", Toast.LENGTH_SHORT).show();
                repeat = 0;
                break;
        }
    }

    private void releasePlayer() {
        if (player == null) return;

        updateStartPosition();
        shouldAutoPlay = player.getPlayWhenReady();
        player.release();
        player = null;
        trackSelector = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) releasePlayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        updateStartPosition();

        outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);
        outState.putInt(KEY_WINDOW, currentWindow);
        outState.putLong(KEY_POSITION, playbackPosition);
        super.onSaveInstanceState(outState);
    }

    private void updateStartPosition() {
        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
    }

    private void updateButtonVisibilities() {
        ivSettings.setVisibility(View.GONE);
        if (player == null) return;

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) return;

        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                    ivSettings.setVisibility(View.VISIBLE);
                    ivSettings.setOnClickListener(this);
                    ivSettings.setTag(i);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo == null) return;

            Pair<AlertDialog, TrackSelectionView> dialogPair = TrackSelectionView.getDialog(this, getString(R.string.video_settings), trackSelector, (int) ivSettings.getTag());
            dialogPair.second.setShowDisableOption(false);
            dialogPair.second.setAllowAdaptiveSelections(true);
            dialogPair.first.show();
        }
    }

    private class PlayerEventListener extends Player.DefaultEventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE: progressBar.setVisibility(View.VISIBLE); break;
                case Player.STATE_BUFFERING: progressBar.setVisibility(View.VISIBLE); break;
                case Player.STATE_READY: progressBar.setVisibility(View.GONE); break;
                case Player.STATE_ENDED: progressBar.setVisibility(View.GONE); break;
            }

            updateButtonVisibilities();
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            updateButtonVisibilities();

            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null)
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS)
                        Toast.makeText(VideoPlayerActivity.this, "Error: This media file isn't supported", Toast.LENGTH_SHORT).show();

                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }
}