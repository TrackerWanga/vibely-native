package com.megan.music.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ─── Music Discovery API Models ────────────────────────
data class Artist(
    val name: String?,
    val country: String?,
    val flag: String?,
    val countryCode: String?,
    val category: String?,
    val songCount: Int?,
    val channel: Channel?,
    val topSongs: List<Song>?
)

data class Channel(
    val id: String?,
    val name: String?,
    val image: String?,
    val subscribers: Long?,
    val verified: Boolean?
)

data class Song(
    val videoId: String?,
    val title: String?,
    val views: Long?,
    val duration: String?,
    val thumbnail: String?
)

data class Country(
    val code: String?,
    val name: String?,
    val flag: String?,
    val continent: String?,
    val totalArtists: Int?,
    val secularArtists: Int?,
    val gospelArtists: Int?
)

data class HomepageData(
    val banner: List<Artist>?,
    val trending: List<Artist>?,
    val topArtists: List<Artist>?,
    val countries: List<Country>?,
    val stats: Stats?
)

data class Stats(
    val artists: Int?,
    val songs: Int?,
    val countries: Int?
)

data class ArtistDetail(
    val artist: Artist?,
    val similar: List<Artist>?
)

// ─── API Interface ────────────────────────────────────
interface MusicDiscoveryApi {
    @GET("api/homepage")
    suspend fun getHomepage(): HomepageData

    @GET("api/countries")
    suspend fun getCountries(): CountryList

    @GET("api/artist/{name}")
    suspend fun getArtist(@Path("name") name: String): ArtistDetail

    @GET("api/search")
    suspend fun search(@Query("q") query: String): SearchResult

    companion object {
        const val BASE_URL = "https://music-discovery-platform.vercel.app/"
    }
}

data class CountryList(val countries: List<Country>?)
data class SearchResult(val local: List<Artist>?)
