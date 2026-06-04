package com.megan.music.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.megan.music.data.api.MeganApi
import com.megan.music.service.MusicService

object PlayerManager {
    fun playSong(context: Context, videoId: String?, title: String?, artist: String?, thumbnail: String?) {
        val url = "${MeganApi.STREAM_URL}?q=${videoId}&type=mp3&apikey=${MeganApi.API_KEY}"
        Log.d("PlayerManager", "Playing: $url")
        
        PlayerState.setTrack(videoId, title, artist, thumbnail)
        MusicService.playUrl = url
        MusicService.playTitle = title
        MusicService.playArtist = artist
        
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
        
        // If service already running, play directly
        MusicService.instance?.play(url, title, artist)
        
        PlayerState.setPlaying(true)
    }

    fun playOffline(context: Context, filePath: String, title: String, artist: String) {
        Log.d("PlayerManager", "Playing offline: $filePath")
        PlayerState.setTrack(null, title, artist, null)
        MusicService.playUrl = filePath
        MusicService.playTitle = title
        MusicService.playArtist = artist
        
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
        MusicService.instance?.play(filePath, title, artist)
        PlayerState.setPlaying(true)
    }
}
