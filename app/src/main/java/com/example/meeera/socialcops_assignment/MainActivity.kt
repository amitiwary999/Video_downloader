package com.example.meeera.socialcops_assignment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var videoUrl : String ?= null
    var proxyVideoUrl : String ?= null
    var player : SimpleExoPlayer ?= null
    var proxyCacheServer : HttpProxyCacheServer ?= null
   // var position : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        proxyCacheServer = SocialCops_Application.getCacheServer(this)
        videoUrl = resources.getString(R.string.video_url)
        if(!(proxyCacheServer?.isCached(videoUrl).toString().toBoolean())){
            if(isConnected()){
                proxyVideoUrl = proxyCacheServer?.getProxyUrl(videoUrl, true)
                start()
            } else{
                Toast.makeText(this, resources.getString(R.string.internet_error), Toast.LENGTH_LONG).show()
            }
        }else {
            proxyVideoUrl = proxyCacheServer?.getProxyUrl(videoUrl, true)
            start()
        }
    }

    override fun onStart() {
        super.onStart()
        if(!(proxyCacheServer?.isCached(videoUrl).toString()).toBoolean()) {
            if (isConnected()) {
                start()
            }
        } else{
            start()
        }
    }

    fun start(){
        createPlayer()
        playerView.player = player
        preparePlayer()
        playerListener()
    }

    fun createPlayer(){
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
    }

    fun preparePlayer(){
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "OfflinePlayer"), bandwidthMeter)
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = ExtractorMediaSource(Uri.parse(proxyVideoUrl),
                dataSourceFactory, extractorsFactory, null, null)
        player?.setPlayWhenReady(true)
        player?.prepare(videoSource)
    }

    fun playerListener(){
       player?.addListener(object : Player.EventListener{
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d("amit", "playbackParameters" + playbackParameters)
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d("amit", "trackSelection"+trackSelections)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == Player.STATE_ENDED){
                    player?.seekTo(0)
                }

                Log.d("amit", "state"+ playWhenReady)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d("amit", "loading"+isLoading)
            }

            override fun onPositionDiscontinuity() {
                Log.d("amit", "seeking")
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d("amit", "repeatmode "+repeatMode)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
                Log.d("amit", "timeline changed "+ timeline)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Toast.makeText(this@MainActivity, R.string.error, Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onStop() {
        super.onStop()
        player?.release()
    }

    fun isConnected() : Boolean{
        val cm = SocialCops_Application.getInstance()?.getApplicationContext()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}
