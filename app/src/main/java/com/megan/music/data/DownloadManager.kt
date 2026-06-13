package com.megan.music.data

import android.app.DownloadManager as AndroidDownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object DownloadManager {
    fun downloadSong(context: Context, videoId: String, title: String) {
        if (!AuthManager.isSignedIn) {
            Toast.makeText(context, "Please sign in to download", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://apis.megan.qzz.io/download/audio?q=$videoId&apikey=megan_admin_master"
        val request = AndroidDownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription("Downloading...")
            .setNotificationVisibility(AndroidDownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${title.replace(" ", "_")}.mp3")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as AndroidDownloadManager
        manager.enqueue(request)
        Toast.makeText(context, "Downloading: $title", Toast.LENGTH_SHORT).show()
    }
}
