package com.megan.music.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.Artist
import com.megan.music.data.api.MeganApi
import com.megan.music.data.api.Song
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
                val response = meganApi.search("gospel worship", MeganApi.API_KEY)
                val gospelArtists = response.results
                    ?.filter { it.author != null }
                    ?.groupBy { it.author ?: "Unknown" }
                    ?.map { (name, songs) ->
                        Artist(
                            name = name,
                            country = "Global",
                            flag = "🙏",
                            countryCode = "GL",
                            category = "gospel",
                            songCount = songs.size,
                            channel = null,
                            topSongs = songs.map { s ->
                                Song(videoId = s.videoId, title = s.title, views = s.views, duration = s.duration, thumbnail = s.thumbnail)
                            }
                        )
                    }?.take(30) ?: emptyList()
                _artists.value = gospelArtists
                _grouped.value = gospelArtists.groupBy { it.country ?: "Global" }
            } catch (e: Exception) { }
            _loading.value = false
        }
    }
}
