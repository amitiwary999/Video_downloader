package com.example.meeera.socialcops_assignment

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer

/**
 * Created by meeera on 16/12/17.
 */
class SocialCops_Application : Application() {

    private var cacheServer: HttpProxyCacheServer? = null
    companion object {
        var application: SocialCops_Application ?= null
        fun getCacheServer(): HttpProxyCacheServer {
            if (application?.cacheServer == null) application?.cacheServer = application?.buildHttpCacheServer()
            return application?.cacheServer as HttpProxyCacheServer
        }
        @Synchronized
        fun getInstance(): SocialCops_Application? {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    private fun buildHttpCacheServer(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this)
                .cacheDirectory(cacheDir)
                .build()
    }
}