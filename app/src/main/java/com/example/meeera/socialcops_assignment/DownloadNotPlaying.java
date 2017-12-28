package com.example.meeera.socialcops_assignment;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by meeera on 24/12/17.
 */

public class DownloadNotPlaying extends AppCompatActivity {
    public String videoURl;
    public Context context;
    public File  direc;
    public String fileName;
    public Button download;
    public SimpleExoPlayerView exoPlayer;
    public SimpleExoPlayer player;
    public DataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadnotplaing);
        download = (Button) findViewById(R.id.perc);
        exoPlayer = (SimpleExoPlayerView) findViewById(R.id.playerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        context = this;
        ContextWrapper contextWrapper = new ContextWrapper(context);
        direc = contextWrapper.getDir("vidDir", Context.MODE_PRIVATE);
        if(getIntent().getExtras() != null) {
            videoURl = getIntent().getExtras().getString("url");
            Log.d("url name", "url "+videoURl);
            fileName = getFileName(videoURl);
            start();
        }else{
            Log.d("url name", "error");
        }

    }

    public void start(){
        createPlayer();
        exoPlayer.setPlayer(player);
        preparePlayer();
        initPlayerListner();
        MediaSource videoSource;
        File flag = new File(direc, fileName+".mp4");
        if(flag.exists()){
            download.setText("downloaded");
            videoSource = new ExtractorMediaSource(Uri.fromFile(flag),
                    dataSourceFactory, extractorsFactory, null, null);
            player.prepare(videoSource);
        } else {
            videoSource = new ExtractorMediaSource(Uri.parse(videoURl), dataSourceFactory, extractorsFactory, null, null);
            player.prepare(videoSource);
            download.setText("download");
            Log.e("amit", "downloading");
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDownloading();
                }
            });
        }
    }

    public void createPlayer(){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory track =  new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(track);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
    }

    public void preparePlayer(){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "OfflinePlayer"), bandwidthMeter);
        extractorsFactory = new DefaultExtractorsFactory();
        player.setPlayWhenReady(true);
    }

    private void initPlayerListner() {
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(DownloadNotPlaying.this,R.string.error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("amit", "downloadnotplaying    onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("amit", "downloadnotplaying    onStop");
    }

    public void startDownloading(){
        new DownloadVideoAsync().execute();
    }

    public class DownloadVideoAsync extends AsyncTask<String, String, String>{
        public File outPutFile = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(videoURl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("amit", "Server returned HTTP " + httpURLConnection.getResponseCode()
                            + " " + httpURLConnection.getResponseMessage());

                }
                int size = httpURLConnection.getContentLength();
                Log.e("amit","size "+httpURLConnection.getContentLength());
                outPutFile = new File(direc, fileName +".mp4");
                if(!outPutFile.exists()){
                    outPutFile.createNewFile();
                }
                Log.d("amit", "path "+outPutFile.getPath());
                Log.d("amit", "check "+fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(outPutFile);
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int lenght;
                int sizeCount = 0;
                while((lenght = inputStream.read(buffer))!=-1){
                    fileOutputStream.write(buffer, 0, lenght);
                    if(size>0){
                        sizeCount = sizeCount+lenght;
                        Log.e("amit", "percentage "+((float)sizeCount/size)*100);
                        publishProgress((int)(((float)sizeCount/size)*100)+"%");
                    }
                }
                inputStream.close();
                fileOutputStream.close();
            } catch (Exception e){
                e.printStackTrace();
                outPutFile = null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.e("perc", values[0]);
            if(values[0].equals("100%")){
                download.setText("downloaded");
            }else {
                download.setText(values[0]);
            }
        }
    }

    public String getFileName(String url){
        Log.d("amit", "path in "+ url.substring(url.lastIndexOf("/")+1));
        return url.substring(url.lastIndexOf("/")+1);
    }
}
