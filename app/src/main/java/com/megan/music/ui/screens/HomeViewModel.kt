package com.megan.music.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.MeganApi
import com.megan.music.data.api.MeganSong
import com.megan.music.data.api.MusicDiscoveryApi
import com.megan.music.data.api.HomepageData
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

    private val _homepage = MutableStateFlow<HomepageData?>(null)
    val homepage: StateFlow<HomepageData?> = _homepage

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadAll()
    }

    private fun loadAll() {
        viewModelScope.launch {
            _loading.value = true
            
            // Load YouTube trending (always works)
            launch {
                try {
                    val response = meganApi.trending(MeganApi.API_KEY)
                    _trending.value = response.results
                        ?.filter { it.videoId != null && it.title != null }
                        ?.take(20)
                        ?: emptyList()
                    Log.d("HomeVM", "Trending loaded: ${_trending.value.size} songs")
                } catch (e: Exception) {
                    Log.e("HomeVM", "Trending error: ${e.message}")
                }
            }
            
            // Load discovery data (may fail, that's ok)
            launch {
                try {
                    val data = discoveryApi.getHomepage()
                    _homepage.value = data
                    Log.d("HomeVM", "Discovery loaded: ${data.banner?.size} banners, ${data.countries?.size} countries")
                } catch (e: Exception) {
                    Log.e("HomeVM", "Discovery error: ${e.message}")
                    // Leave homepage as null - UI will show trending only
                }
            }
            
            _loading.value = false
        }
    }

    fun playYouTube(song: MeganSong) { }
}
