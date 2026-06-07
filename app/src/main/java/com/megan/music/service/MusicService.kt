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

class MusicService : Service() {
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    var currentTitle: String? = null
    var currentArtist: String? = null
    var playing = false

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
            "STOP" -> { stopAll(); return START_NOT_STICKY }
        }
        intent?.let {
            play(it.getStringExtra("url") ?: return START_STICKY, it.getStringExtra("title"), it.getStringExtra("artist"))
        }
        return START_STICKY
    }

    fun play(url: String, title: String?, artist: String?) {
        currentTitle = title; currentArtist = artist
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { start(); playing = true; showNotification() }
            setOnErrorListener { _, _, _ -> false }
            setOnCompletionListener { playing = false; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
            prepareAsync()
        }
    }

    fun pause() { mediaPlayer?.pause(); playing = false; showNotification() }
    fun resume() { mediaPlayer?.start(); playing = true; showNotification() }
    fun stopAll() { mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null; playing = false; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }

    private fun showNotification() {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val toggleIntent = PendingIntent.getService(this, 1, Intent(this, MusicService::class.java).setAction(if (playing) "PAUSE" else "PLAY"), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val stopIntent = PendingIntent.getService(this, 2, Intent(this, MusicService::class.java).setAction("STOP"), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val nb = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "megan_playback")
        } else {
            @Suppress("DEPRECATION") Notification.Builder(this)
        }

        val notification = nb
            .setContentTitle(currentTitle ?: "Megan Music")
            .setContentText(currentArtist ?: "Playing")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pi)
            .setOngoing(true)
            .addAction(if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, if (playing) "Pause" else "Play", toggleIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() { mediaPlayer?.release(); mediaPlayer = null; super.onDestroy() }
}
