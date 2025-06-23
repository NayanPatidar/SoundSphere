// In a new file: presentation/viewmodel/SongViewModel.kt
package com.example.soundsphere.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundsphere.data.repository.MusicRepositoryImpl
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class SongViewModel() : ViewModel() {

    private var cachedSongData: Resource<JsonObject>? = null
    private val repository = MusicRepositoryImpl()

    private val _songData = MutableLiveData<Resource<JsonObject>>()
    val songData: LiveData<Resource<JsonObject>> get() = _songData

    fun loadSongData(songId: String, forceRefresh: Boolean = false) {

        if (!forceRefresh && cachedSongData != null) {
            _songData.value = cachedSongData ?: Resource.Error("No cached data")
            return
        }

        viewModelScope.launch {
            repository.getSongDetails(songId).collect { resource ->
                _songData.value = resource
                if (resource is Resource.Success) {
                    cachedSongData = resource
                }
            }
        }
    }
}
