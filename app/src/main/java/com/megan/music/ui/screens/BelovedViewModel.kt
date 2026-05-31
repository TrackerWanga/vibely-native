package com.megan.music.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.Artist
import com.megan.music.data.api.Song
import com.megan.music.data.api.MusicDiscoveryApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BelovedViewModel @Inject constructor(
    private val discoveryApi: MusicDiscoveryApi
) : ViewModel() {
    private val _beloved = MutableStateFlow<List<Artist>>(emptyList())
    val beloved: StateFlow<List<Artist>> = _beloved

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadBeloved() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Use homepage trending artists as "beloved"
                val response = discoveryApi.getHomepage()
                _beloved.value = response.trending ?: emptyList()
            } catch (e: Exception) { }
            _loading.value = false
        }
    }
}
