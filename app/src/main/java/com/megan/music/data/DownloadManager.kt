package com.megan.music.data

import android.app.DownloadManager as AndroidDownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object DownloadManager {
    fun downloadSong(context: Context, videoId: String, title: String, onNeedAuth: () -> Unit = {}) {
        if (!AuthManager.isSignedIn) {
            Toast.makeText(context, "⚠ Sign in required to download", Toast.LENGTH_LONG).show()
            onNeedAuth()
            return
        }

        try {
            val url = "https://apis.megan.qzz.io/download/audio?q=$videoId&apikey=megan_admin_master"
            val request = AndroidDownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription("Downloading ${title}...")
                .setNotificationVisibility(AndroidDownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MeganMusic/${title.replace(" ", "_")}.mp3")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as AndroidDownloadManager
            manager.enqueue(request)
            Toast.makeText(context, "📥 Downloading: $title", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
