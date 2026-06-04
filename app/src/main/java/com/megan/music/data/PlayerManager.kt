package com.megan.music.data

import android.content.Context
import android.content.Intent
import com.megan.music.data.api.MeganApi
import com.megan.music.service.MusicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PlayerManager {
    fun playSong(context: Context, videoId: String?, title: String?, artist: String?, thumbnail: String?) {
        val url = "${MeganApi.STREAM_URL}?q=${videoId}&type=mp3&apikey=${MeganApi.API_KEY}"
        PlayerState.setTrack(videoId, title, artist, thumbnail)
        MusicService.playUrl = url
        MusicService.playTitle = title
        MusicService.playArtist = artist
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
        PlayerState.setPlaying(true)
    }

    fun playOffline(context: Context, filePath: String, title: String, artist: String) {
        PlayerState.setTrack(null, title, artist, null)
        MusicService.playUrl = filePath
        MusicService.playTitle = title
        MusicService.playArtist = artist
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
        PlayerState.setPlaying(true)
    }
}
