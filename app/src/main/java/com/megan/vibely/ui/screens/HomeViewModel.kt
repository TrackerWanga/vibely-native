package com.megan.vibely.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.vibely.data.api.MeganApi
import com.megan.vibely.data.api.Song
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

    init { loadTrending() }

    private fun loadTrending() {
        viewModelScope.launch {
            try {
                val response = api.trending(MeganApi.API_KEY)
                _trending.value = response.results?.filter { it.videoId != null } ?: emptyList()
            } catch (e: Exception) { }
            _loading.value = false
        }
    }

    fun play(song: Song) {
        // Will be connected to MusicService via MediaController
    }
}
