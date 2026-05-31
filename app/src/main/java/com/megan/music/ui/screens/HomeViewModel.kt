package com.megan.music.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val meganApi: MeganApi,
    private val discoveryApi: MusicDiscoveryApi
) : ViewModel() {
    private val _trending = MutableStateFlow<List<MeganSong>>(emptyList())
    val trending: StateFlow<List<MeganSong>> = _trending

    private val _homepage = MutableStateFlow<HomepageResponse?>(null)
    val homepage: StateFlow<HomepageResponse?> = _homepage

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    // Country songs
    private val _countrySongs = MutableStateFlow<Map<String, List<MeganSong>>>(emptyMap())
    val countrySongs: StateFlow<Map<String, List<MeganSong>>> = _countrySongs

    private val _visibleCountries = MutableStateFlow<List<Country>>(emptyList())
    val visibleCountries: StateFlow<List<Country>> = _visibleCountries

    private var allCountries: List<Country> = emptyList()
    private var currentPage = 1
    private val perPage = 4

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            _loading.value = true
            // Load discovery homepage
            try {
                val response = discoveryApi.getHomepage()
                _homepage.value = response
                allCountries = response.countries ?: emptyList()
                loadMoreCountries()
            } catch (e: Exception) { Log.e("HomeVM", "Discovery error: ${e.message}") }
            // Load YouTube trending
            try {
                val yt = meganApi.trending(MeganApi.API_KEY)
                _trending.value = yt.results?.filter { it.videoId != null }?.take(20) ?: emptyList()
            } catch (e: Exception) { Log.e("HomeVM", "Trending error: ${e.message}") }
            _loading.value = false
        }
    }

    fun loadMoreCountries() {
        val start = (currentPage - 1) * perPage
        val end = minOf(start + perPage, allCountries.size)
        if (start >= allCountries.size) return

        val batch = allCountries.subList(start, end)
        _visibleCountries.value = _visibleCountries.value + batch
        currentPage++

        // Load songs for each new country
        viewModelScope.launch {
            val current = _countrySongs.value.toMutableMap()
            for (country in batch) {
                try {
                    val response = meganApi.search("${country.name} music", MeganApi.API_KEY)
                    val songs = response.results?.filter { it.videoId != null }?.take(10) ?: emptyList()
                    current[country.code ?: country.name ?: ""] = songs
                } catch (e: Exception) {
                    current[country.code ?: country.name ?: ""] = emptyList()
                }
            }
            _countrySongs.value = current
        }
    }

    fun playYouTube(song: MeganSong) {}
}
