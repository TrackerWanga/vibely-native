package com.megan.music.data

import android.app.DownloadManager as AndroidDownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DownloadManager {
    suspend fun downloadSong(context: Context, videoId: String, title: String, onNeedAuth: () -> Unit = {}) {
        if (!AuthManager.isSignedIn) {
            Toast.makeText(context, "⚠ Sign in required to download", Toast.LENGTH_LONG).show()
            onNeedAuth()
            return
        }

        try {
            Toast.makeText(context, "🔍 Fetching download link...", Toast.LENGTH_SHORT).show()

            // Step 1: Call Megan API to get the download URL
            val downloadUrl = withContext(Dispatchers.IO) {
                val apiUrl = "https://apis.megan.qzz.io/download/audio?q=$videoId&apikey=megan_admin_master"
                val connection = URL(apiUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val json = JSONObject(response)
                // Get the actual MP3 URL from the JSON response
                json.optString("downloadUrl", "")
                    .ifEmpty { json.optString("proxyUrl", "") }
            }

            if (downloadUrl.isEmpty()) {
                Toast.makeText(context, "❌ Could not get download link", Toast.LENGTH_SHORT).show()
                return
            }

            // Step 2: Download the actual MP3 file
            val safeTitle = title.replace(Regex("[^a-zA-Z0-9 ]"), "").trim().replace(" ", "_")
            val request = AndroidDownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle("Megan Music - $title")
                .setDescription("Downloading...")
                .setNotificationVisibility(AndroidDownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MeganMusic/$safeTitle.mp3")
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
