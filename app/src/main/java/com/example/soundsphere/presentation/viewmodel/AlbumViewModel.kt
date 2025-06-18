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

class AlbumViewModel : ViewModel() {

    private var cachedAlbumData: Resource<JsonObject>? = null
    private val repository = MusicRepositoryImpl()

    private val _albumData = MutableLiveData<Resource<JsonObject>>()
    val albumData: LiveData<Resource<JsonObject>> = _albumData

    fun loadAlbumData(albumId: String, forceRefresh: Boolean = false) {

        if (!forceRefresh && cachedAlbumData != null) {
            _albumData.value = cachedAlbumData ?: Resource.Error("No cached data")
            return
        }

        viewModelScope.launch {
            repository.getAlbumDetails(albumId).collect { resource ->
                _albumData.value = resource
                if (resource is Resource.Success) {
                    cachedAlbumData = resource
                }
            }
        }
    }
}
