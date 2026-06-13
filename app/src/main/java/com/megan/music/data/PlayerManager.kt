package com.megan.music.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.megan.music.service.MusicService

object PlayerManager {
    private var service: MusicService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as MusicService.MusicBinder).getService()
            PlayerState.setPlaying(service?.playing ?: false)
        }
        override fun onServiceDisconnected(name: ComponentName?) { service = null }
    }

    fun play(context: Context, videoId: String, title: String?, artist: String?, thumbnail: String?) {
        // Reset and set new track
        PlayerState.setTrack(videoId, title, artist, thumbnail)
        PlayerState.setLoading(true)
        PlayerState.setPlaying(false)

        val url = if (videoId.startsWith("/")) videoId
                  else "https://apis.megan.qzz.io/stream?q=$videoId&type=mp3&apikey=megan_admin_master"

        service?.stopAll()

        val intent = Intent(context, MusicService::class.java).apply {
            putExtra("url", url)
            putExtra("title", title)
            putExtra("artist", artist)
        }
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun toggle() {
        if (service?.playing == true) { service?.pause(); PlayerState.setPlaying(false) }
        else { service?.resume(); PlayerState.setPlaying(true) }
    }

    fun stop() { service?.stopAll(); PlayerState.setPlaying(false); PlayerState.setLoading(false) }
}
