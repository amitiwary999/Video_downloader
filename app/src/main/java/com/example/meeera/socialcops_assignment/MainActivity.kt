package com.example.meeera.socialcops_assignment

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
    var position : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var proxyCacheServer : HttpProxyCacheServer = SocialCops_Application.getCacheServer(this)
        videoUrl = resources.getString(R.string.video_url)
        proxyVideoUrl = proxyCacheServer.getProxyUrl(videoUrl, true)
    }

    override fun onStart() {
        super.onStart()
        createPlayer()
        playerView.player = player
        player?.seekTo(position)
        preparePlayer()
        playerListener()
    }

    fun createPlayer(){
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
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
       /* player?.addListener(object : ExoPlayer.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onTimelineChanged(timeline: Timeline, manifest: Any) {

            }

            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Toast.makeText(this@MainActivity, R.string.error, Toast.LENGTH_LONG).show()
            }

            override fun onPositionDiscontinuity() {

            }
        })*/
    }

    override fun onStop() {
        super.onStop()
        player?.release()
    }
}
