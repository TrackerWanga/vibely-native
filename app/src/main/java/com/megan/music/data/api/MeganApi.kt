package com.megan.music.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class MeganSong(
    val videoId: String?,
    val title: String?,
    val author: String?,
    val thumbnail: String?,
    val duration: String?,
    val views: Long?
)

data class SearchResponse(val success: Boolean?, val results: List<MeganSong>?)
data class TrendingResponse(val success: Boolean?, val results: List<MeganSong>?)

interface MeganApi {
    @GET("api/search/youtube")
    suspend fun search(@Query("q") query: String, @Query("apikey") key: String): SearchResponse

    @GET("api/music/trending")
    suspend fun trending(@Query("apikey") key: String): TrendingResponse

    companion object {
        const val BASE_URL = "https://apis.megan.qzz.io/"
        const val API_KEY = "megan_admin_master"
    }
}
