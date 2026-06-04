package com.megan.music.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.megan.music.service.MusicService

object PlayerManager {
    private var service: MusicService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as MusicService.MusicBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) { service = null }
    }

    fun playSong(context: Context, videoId: String, title: String?, artist: String?, thumbnail: String?) {
        PlayerState.setTrack(videoId, title, artist, thumbnail)
        val url = "https://apis.megan.qzz.io/stream?q=${videoId}&type=mp3&apikey=megan_admin_master"
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra("url", url)
            putExtra("title", title)
            putExtra("artist", artist)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        context.startForegroundService(intent)
        PlayerState.setPlaying(true)
    }

    fun togglePlayPause() {
        if (service?.isPlaying == true) service?.pause() else service?.resume()
        PlayerState.setPlaying(service?.isPlaying ?: false)
    }

    fun stop() {
        service?.stop()
        PlayerState.setPlaying(false)
    }
}
