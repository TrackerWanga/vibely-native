package com.megan.music.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.MeganApi
import com.megan.music.data.api.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: MeganApi
) : ViewModel() {
    private val _trending = MutableStateFlow<List<Song>>(emptyList())
    val trending: StateFlow<List<Song>> = _trending

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadTrending()
    }

    private fun loadTrending() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.trending(MeganApi.API_KEY)
                _trending.value = response.results
                    ?.filter { it.videoId != null && it.title != null }
                    ?.take(20)
                    ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _loading.value = false
        }
    }

    fun play(song: Song) {
        // Will be connected to MusicService
    }
}
