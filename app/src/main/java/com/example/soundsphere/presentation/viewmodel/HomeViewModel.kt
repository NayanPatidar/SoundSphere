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

class HomeViewModel : ViewModel() {

    private var cachedHomeData: Resource<JsonObject>? = null
    private val repository = MusicRepositoryImpl()

    private val _homeData = MutableLiveData<Resource<JsonObject>>()
    val homeData: LiveData<Resource<JsonObject>> = _homeData

    fun loadHomeData(forceRefresh: Boolean = false) {

        if (!forceRefresh && cachedHomeData != null) {
            _homeData.value = cachedHomeData!!
            return
        }

        viewModelScope.launch {
            repository.getHomeData().collect { resource ->
                _homeData.value = resource
                if (resource is Resource.Success) {
                    cachedHomeData = resource
                }
            }
        }
    }
}
