package com.megan.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.megan.music.MainActivity
import com.megan.music.data.PlayerState

class MusicService : Service() {
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    var currentTitle: String? = null
    var currentArtist: String? = null
    var playing = false
    var onComplete: (() -> Unit)? = null
    var onNext: (() -> Unit)? = null
    var onPrev: (() -> Unit)? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("megan_playback", "Megan Music", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "PAUSE" -> { pause(); return START_STICKY }
            "PLAY" -> { resume(); return START_STICKY }
            "NEXT" -> { onNext?.invoke(); return START_STICKY }
            "PREV" -> { onPrev?.invoke(); return START_STICKY }
            "STOP" -> { stopAll(); return START_NOT_STICKY }
        }
        intent?.let { play(it.getStringExtra("url") ?: return START_STICKY, it.getStringExtra("title"), it.getStringExtra("artist")) }
        return START_STICKY
    }

    fun play(url: String, title: String?, artist: String?) {
        currentTitle = title; currentArtist = artist
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null; playing = false
        showNotification(isLoading = true)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { start(); playing = true; PlayerState.setLoading(false); PlayerState.setPlaying(true); showNotification() }
            setOnErrorListener { _, _, _ -> playing = false; PlayerState.setLoading(false); PlayerState.setPlaying(false); showNotification(); false }
            setOnCompletionListener { playing = false; PlayerState.setPlaying(false); onComplete?.invoke(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
            prepareAsync()
        }
    }

    fun pause() { mediaPlayer?.pause(); playing = false; PlayerState.setPlaying(false); showNotification() }
    fun resume() { mediaPlayer?.start(); playing = true; PlayerState.setPlaying(true); showNotification() }
    fun stopAll() { mediaPlayer?.apply { if (isPlaying) stop(); release() }; mediaPlayer = null; playing = false; PlayerState.setPlaying(false); PlayerState.setLoading(false); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }

    private fun showNotification(isLoading: Boolean = false) {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val nb = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, "megan_playback")
        else { @Suppress("DEPRECATION") Notification.Builder(this) }

        val notif = nb
            .setContentTitle(currentTitle ?: "Megan Music")
            .setContentText(if (isLoading) "Loading..." else currentArtist ?: "Playing")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pi).setOngoing(true)
            .setProgress(0, 0, isLoading)

        // Add media control buttons
        notif.addAction(android.R.drawable.ic_media_previous, "Prev", PendingIntent.getService(this, 4, Intent(this, MusicService::class.java).setAction("PREV"), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
        notif.addAction(if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, if (playing) "Pause" else "Play", PendingIntent.getService(this, 1, Intent(this, MusicService::class.java).setAction(if (playing) "PAUSE" else "PLAY"), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
        notif.addAction(android.R.drawable.ic_media_next, "Next", PendingIntent.getService(this, 3, Intent(this, MusicService::class.java).setAction("NEXT"), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        startForeground(1, notif.build())
    }

    override fun onDestroy() { mediaPlayer?.release(); mediaPlayer = null; super.onDestroy() }
}
