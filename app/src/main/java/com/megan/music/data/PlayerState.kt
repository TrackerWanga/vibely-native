package com.megan.music.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PlayerState {
    private val _currentTitle = MutableStateFlow("Select a song")
    val currentTitle: StateFlow<String> = _currentTitle
    private val _currentArtist = MutableStateFlow("Browse trending or search")
    val currentArtist: StateFlow<String> = _currentArtist
    private val _currentThumbnail = MutableStateFlow("")
    val currentThumbnail: StateFlow<String> = _currentThumbnail
    private val _currentVideoId = MutableStateFlow("")
    val currentVideoId: StateFlow<String> = _currentVideoId
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setTrack(videoId: String?, title: String?, artist: String?, thumbnail: String?) {
        _currentVideoId.value = videoId ?: ""
        _currentTitle.value = title ?: "Unknown"
        _currentArtist.value = artist ?: "Unknown Artist"
        _currentThumbnail.value = thumbnail ?: ""
    }
    fun setPlaying(playing: Boolean) { _isPlaying.value = playing }
    fun setLoading(loading: Boolean) { _isLoading.value = loading }
    fun reset() {
        _currentTitle.value = "Select a song"
        _currentArtist.value = "Browse trending or search"
        _currentThumbnail.value = ""
        _currentVideoId.value = ""
        _isPlaying.value = false
        _isLoading.value = false
    }
}
