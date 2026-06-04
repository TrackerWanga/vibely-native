package com.megan.music.data

import android.content.Context
import android.content.Intent
import com.megan.music.data.api.MeganApi
import com.megan.music.service.MusicService

object PlayerManager {
    fun playSong(context: Context, videoId: String?, title: String?, artist: String?) {
        val url = "${MeganApi.STREAM_URL}?q=${videoId}&type=mp3&apikey=${MeganApi.API_KEY}"
        MusicService.playUrl = url
        MusicService.playTitle = title
        MusicService.playArtist = artist
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
    }
}
