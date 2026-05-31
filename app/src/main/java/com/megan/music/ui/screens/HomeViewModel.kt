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

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = discoveryApi.getHomepage()
                Log.d("HomeVM", "Discovery success: ${response.banner?.size} banners")
                _homepage.value = response
            } catch (e: Exception) {
                Log.e("HomeVM", "Discovery failed: ${e.message}", e)
            }
            try {
                val yt = meganApi.trending(MeganApi.API_KEY)
                _trending.value = yt.results?.filter { it.videoId != null }?.take(20) ?: emptyList()
            } catch (e: Exception) {
                Log.e("HomeVM", "Trending failed: ${e.message}", e)
            }
            _loading.value = false
        }
    }

    fun playYouTube(song: MeganSong) {}
}
