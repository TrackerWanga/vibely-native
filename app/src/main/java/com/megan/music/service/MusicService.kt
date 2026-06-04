package com.megan.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
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
        Log.d("MusicService", "=== onCreate ===")

        // Create notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "megan_playback",
                "Megan Music",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

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
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    Log.d("MusicService", "State: $state")
                }
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e("MusicService", "Player error: ${error.message}", error)
                }
            })
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

        // Start foreground immediately
        startForeground(1, createNotification())

        // Play if URL was set
        playUrl?.let { url -> play(url, playTitle, playArtist) }

        instance = this
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "megan_playback")
                .setContentTitle(playTitle ?: "Megan Music")
                .setContentText(playArtist ?: "Streaming")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle(playTitle ?: "Megan Music")
                .setContentText(playArtist ?: "Streaming")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("MusicService", "onGetSession")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {}

    fun play(url: String, title: String?, artist: String?) {
        Log.d("MusicService", "play() called with: $url")
        playUrl = url
        playTitle = title
        playArtist = artist

        player?.apply {
            stop()
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            play()
            Log.d("MusicService", "play() completed, isPlaying: $isPlaying")
        }
    }

    override fun onDestroy() {
        Log.d("MusicService", "onDestroy")
        mediaSession?.release()
        player?.release()
        instance = null
        super.onDestroy()
    }

    companion object {
        var playUrl: String? = null
        var playTitle: String? = null
        var playArtist: String? = null
        var instance: MusicService? = null
    }
}
