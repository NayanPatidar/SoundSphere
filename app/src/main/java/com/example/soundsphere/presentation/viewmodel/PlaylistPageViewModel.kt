// File: presentation/viewmodel/HomeViewModel.kt
package com.example.soundsphere.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundsphere.data.repository.MusicRepositoryImpl
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class PlaylistPageViewModel : ViewModel() {

    private var cachedPlaylistData: Resource<JsonObject>? = null
    private val repository = MusicRepositoryImpl()

    private val _playlistData = MutableLiveData<Resource<JsonObject>>()
    val playlistData: LiveData<Resource<JsonObject>> = _playlistData

    fun loadPlaylistData(playlistId: String, forceRefresh: Boolean = false) {

        if (!forceRefresh && cachedPlaylistData != null) {
            _playlistData.value = cachedPlaylistData ?: Resource.Error("No cached data")
            return
        }

        viewModelScope.launch {
            repository.getPlaylistDetails(playlistId).collect { resource ->
                _playlistData.value = resource
                if (resource is Resource.Success) {
                    cachedPlaylistData = resource
                }
            }
        }
    }
}
