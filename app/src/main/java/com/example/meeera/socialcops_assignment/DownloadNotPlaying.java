package com.example.meeera.socialcops_assignment;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by meeera on 24/12/17.
 */

public class DownloadNotPlaying extends AppCompatActivity {
    private VideoView videoView;
    public String videoURl;
    public Context context;
    public File outFile, direc;
    public String fileName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadnotplaing);
        videoView = (VideoView)findViewById(R.id.videoview);
        context = this;
        ContextWrapper contextWrapper = new ContextWrapper(context);
        direc = contextWrapper.getDir("vidDir", Context.MODE_PRIVATE);
        videoURl = getResources().getString(R.string.video_url);
        fileName = getFileName(videoURl);
        File flag = new File(direc, fileName+".mp4");
        if(flag.exists()){
            videoView.setVideoURI(Uri.fromFile(flag));
            videoView.start();
        } else {
            Log.e("amit", "downloading");
            startDownloading();
        }
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
            if(outPutFile!=null){
                outFile = outPutFile;
                videoView.setVideoURI(Uri.fromFile(outFile));
                videoView.start();
            }
        }
    }

    public String getFileName(String url){
        Log.d("amit", "path in "+ url.substring(url.lastIndexOf("/")+1));
        return url.substring(url.lastIndexOf("/")+1);
    }
}
