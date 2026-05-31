package com.megan.music.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.Artist
import com.megan.music.data.api.MeganApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GospelViewModel @Inject constructor(
    private val meganApi: MeganApi
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
                // Load from discovery API via the gospel category
                val response = meganApi.search("gospel worship", MeganApi.API_KEY)
                // For now create mock gospel artists from search results
                val gospelArtists = response.results?.map { song ->
                    Artist(
                        name = song.author ?: "Gospel Artist",
                        country = "Global",
                        flag = "🙏",
                        songCount = 5,
                        channel = null,
                        topSongs = listOf(
                            Song(song.videoId, song.title, song.views, song.duration, song.thumbnail)
                        )
                    )
                }?.distinctBy { it.name }?.take(30) ?: emptyList()
                _artists.value = gospelArtists
                _grouped.value = gospelArtists.groupBy { it.country ?: "Global" }
            } catch (e: Exception) { }
            _loading.value = false
        }
    }
}
