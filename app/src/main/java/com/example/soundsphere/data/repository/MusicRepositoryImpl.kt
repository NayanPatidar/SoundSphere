package com.example.soundsphere.data.repository

import com.example.soundsphere.data.models.Album
import com.example.soundsphere.data.models.Artist
import com.example.soundsphere.data.models.HomeResponse
import com.example.soundsphere.data.models.Modules
import com.example.soundsphere.data.remote.NetworkClient
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicRepositoryImpl : MusicRepository {
    private val apiService = NetworkClient.musicApiService

    override suspend fun getHomeData(): Flow<Resource<JsonObject>> = flow {
        emit(Resource.Loading<JsonObject>())
        try {
            val response = apiService.getHomeData()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<JsonObject>(response.body()!!))
            } else {
                // Handle unsuccessful response
                emit(Resource.Error<JsonObject>("Failed to load home data: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<JsonObject>(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getAlbumDetails(albumId: String): Flow<Resource<JsonObject>> = flow {
        emit(Resource.Loading<JsonObject>())
        try {
            val response = apiService.getAlbumDetails(albumId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<JsonObject>(response.body()!!))
            } else {
                emit(Resource.Error<JsonObject>("Failed to load albums: ${response}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<JsonObject>(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getArtists(): Flow<Resource<JsonObject>> = flow {
        emit(Resource.Loading<JsonObject>())
        try {
            val response = apiService.getArtists()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<JsonObject>(response.body()!!))
            } else {
                emit(Resource.Error<JsonObject>("Failed to load artists: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<JsonObject>(e.message ?: "An error occurred"))
        }
    }
}