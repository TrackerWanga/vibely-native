package com.megan.music.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.Artist
import com.megan.music.data.api.MusicDiscoveryApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GospelViewModel @Inject constructor(
    private val discoveryApi: MusicDiscoveryApi
) : ViewModel() {
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _grouped = MutableStateFlow<Map<String, List<Artist>>>(emptyMap())
    val groupedArtists: StateFlow<Map<String, List<Artist>>> = _grouped

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadGospel() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = discoveryApi.getHomepage()
                val all = ((response.banner ?: emptyList()) + (response.trending ?: emptyList()) + (response.topArtists ?: emptyList()))
                    .distinctBy { it.name }
                    .take(50)
                _artists.value = all
                _grouped.value = all.groupBy { it.country ?: "Global" }
                Log.d("GospelVM", "Loaded ${all.size} artists in ${_grouped.value.size} countries")
            } catch (e: Exception) {
                Log.e("GospelVM", "Error: ${e.message}")
                _artists.value = emptyList()
                _grouped.value = emptyMap()
            }
            _loading.value = false
        }
    }
}
