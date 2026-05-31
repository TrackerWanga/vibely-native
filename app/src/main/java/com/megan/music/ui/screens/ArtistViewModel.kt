package com.megan.music.ui.screens

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
class ArtistViewModel @Inject constructor(
    private val discoveryApi: MusicDiscoveryApi
) : ViewModel() {
    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist

    private val _similar = MutableStateFlow<List<Artist>>(emptyList())
    val similar: StateFlow<List<Artist>> = _similar

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadArtist(name: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = discoveryApi.getArtist(name)
                _artist.value = response.artist
                _similar.value = response.similar ?: emptyList()
            } catch (e: Exception) { }
            _loading.value = false
        }
    }
}
