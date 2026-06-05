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
import android.util.Log
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
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        intent?.let {
            val url = it.getStringExtra("url")
            val title = it.getStringExtra("title")
            val artist = it.getStringExtra("artist")
            if (url != null) play(url, title, artist)
        }
        return START_STICKY
    }

    fun play(url: String, title: String?, artist: String?) {
        currentTitle = title
        currentArtist = artist
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { start(); playing = true; updateNotification() }
            setOnErrorListener { _, _, _ -> false }
            setOnCompletionListener { playing = false; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
            prepareAsync()
        }
    }

    fun pause() { mediaPlayer?.pause(); playing = false }
    fun resume() { mediaPlayer?.start(); playing = true }
    fun stopAll() { mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null; playing = false; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("megan_playback", "Megan Music", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun buildNotification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "megan_playback").setContentTitle(currentTitle ?: "Megan Music").setContentText(currentArtist ?: "Playing").setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pi).setOngoing(true).build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this).setContentTitle(currentTitle ?: "Megan Music").setContentText(currentArtist ?: "Playing").setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pi).setOngoing(true).build()
        }
    }

    private fun updateNotification() { getSystemService(NotificationManager::class.java).notify(1, buildNotification()) }
    override fun onDestroy() { mediaPlayer?.release(); mediaPlayer = null; super.onDestroy() }
}
