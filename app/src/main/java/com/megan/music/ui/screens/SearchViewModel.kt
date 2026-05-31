package com.megan.music.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.megan.music.data.api.MeganApi
import com.megan.music.data.api.MeganSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: MeganApi
) : ViewModel() {
    private val _results = MutableStateFlow<List<MeganSong>>(emptyList())
    val results: StateFlow<List<MeganSong>> = _results

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun search(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.search(query, MeganApi.API_KEY)
                _results.value = response.results?.take(20) ?: emptyList()
            } catch (e: Exception) { }
            _loading.value = false
        }
    }
}
