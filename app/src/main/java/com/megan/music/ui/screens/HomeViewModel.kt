package com.megan.music.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val meganApi: MeganApi,
    private val discoveryApi: MusicDiscoveryApi
) : ViewModel() {
    val trending = MutableStateFlow<List<MeganSong>>(emptyList())
    val homepage = MutableStateFlow<HomepageResponse?>(null)
    val loading = MutableStateFlow(true)
    val countrySongs = MutableStateFlow<Map<String, List<MeganSong>>>(emptyMap())
    val visibleCountries = MutableStateFlow<List<Country>>(emptyList())

    private var allCountries: List<Country> = emptyList()
    private var currentPage = 0
    private val perPage = 3
    private var isLoadingCountries = false

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            loading.value = true
            withTimeoutOrNull(15000) {
                try {
                    homepage.value = discoveryApi.getHomepage()
                    allCountries = homepage.value?.countries ?: emptyList()
                    loadMoreCountries()
                } catch (e: Exception) { Log.e("HomeVM", "Discovery: ${e.message}") }
                try {
                    trending.value = meganApi.trending(MeganApi.API_KEY).results
                        ?.filter { it.videoId != null }?.take(20) ?: emptyList()
                } catch (e: Exception) { Log.e("HomeVM", "Trending: ${e.message}") }
            }
            loading.value = false
        }
    }

    fun loadMoreCountries() {
        if (isLoadingCountries || currentPage * perPage >= allCountries.size) return
        isLoadingCountries = true
        val start = currentPage * perPage
        val end = minOf(start + perPage, allCountries.size)
        val batch: List<Country> = allCountries.subList(start, end)
        visibleCountries.value = visibleCountries.value + batch
        currentPage++

        viewModelScope.launch {
            for (country: Country in batch) {
                try {
                    val key: String = country.code ?: country.name ?: ""
                    val response: ArtistsResponse = discoveryApi.getArtistsByCountry(countryCode = country.code ?: "", limit = 5)
                    val songs: List<MeganSong> = response.artists
                        ?.flatMap { artist: Artist -> artist.topSongs ?: emptyList() }
                        ?.filter { song: Song -> song.videoId != null }
                        ?.distinctBy { song: Song -> song.videoId }
                        ?.take(16)
                        ?.map { song: Song ->
                            MeganSong(
                                videoId = song.videoId,
                                title = song.title,
                                author = null,
                                thumbnail = song.thumbnail,
                                duration = song.duration,
                                views = song.views
                            )
                        } ?: emptyList()
                    val current: MutableMap<String, List<MeganSong>> = countrySongs.value.toMutableMap()
                    current[key] = songs
                    countrySongs.value = current
                } catch (e: Exception) {
                    val current: MutableMap<String, List<MeganSong>> = countrySongs.value.toMutableMap()
                    current[country.code ?: country.name ?: ""] = emptyList()
                    countrySongs.value = current
                }
            }
            isLoadingCountries = false
        }
    }

    fun playYouTube(song: MeganSong) {}
}
