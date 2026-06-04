package com.megan.music.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.megan.music.MainActivity

class MusicService : MediaSessionService() {
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicService", "onCreate")

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                false
            )
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(pendingIntent)
            .build()

        // Play if URL was set before service started
        playUrl?.let { url ->
            play(url, playTitle, playArtist)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("MusicService", "onGetSession")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Keep playing
    }

    fun play(url: String, title: String?, artist: String?) {
        Log.d("MusicService", "play: $url")
        player?.apply {
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(title ?: "Unknown")
                        .setArtist(artist ?: "Unknown Artist")
                        .build()
                )
                .build()
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        playUrl = url
        playTitle = title
        playArtist = artist
    }

    override fun onDestroy() {
        mediaSession?.release()
        player?.release()
        super.onDestroy()
    }

    companion object {
        var playUrl: String? = null
        var playTitle: String? = null
        var playArtist: String? = null
        var instance: MusicService? = null
    }
}
