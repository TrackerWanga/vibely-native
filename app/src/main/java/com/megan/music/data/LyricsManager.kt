package com.megan.music.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object LyricsManager {
    suspend fun fetchLyrics(title: String, artist: String): String? {
        if (!AuthManager.isSignedIn) return null

        return withContext(Dispatchers.IO) {
            try {
                val query = URLEncoder.encode("$artist $title", "UTF-8")
                val url = "https://apis.megan.qzz.io/download/lyrics?q=$query&apikey=megan_admin_master"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val json = JSONObject(response)
                if (json.optBoolean("success", false)) {
                    json.optString("lyrics", null) ?: json.optString("syncedLyrics", null)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
