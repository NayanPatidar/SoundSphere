package com.example.soundsphere.data.repository

import com.example.soundsphere.data.models.Album
import com.example.soundsphere.data.models.Artist
import com.example.soundsphere.data.models.Modules
import com.example.soundsphere.data.remote.NetworkClient
import com.example.soundsphere.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicRepositoryImpl : MusicRepository {
    private val apiService = NetworkClient.musicApiService

    override suspend fun getHomeData(): Flow<Resource<List<Modules>>> = flow {
        emit(Resource.Loading<List<Modules>>())
        try {
            val response = apiService.getHomeData()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<List<Modules>>(response.body()!!))
            } else {
                // Handle unsuccessful response
                emit(Resource.Error<List<Modules>>("Failed to load home data: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<List<Modules>>(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getAlbums(): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading<List<Album>>())
        try {
            val response = apiService.getAlbums()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<List<Album>>(response.body()!!))
            } else {
                emit(Resource.Error<List<Album>>("Failed to load albums: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<List<Album>>(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getArtists(): Flow<Resource<List<Artist>>> = flow {
        emit(Resource.Loading<List<Artist>>())
        try {
            val response = apiService.getArtists()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success<List<Artist>>(response.body()!!))
            } else {
                emit(Resource.Error<List<Artist>>("Failed to load artists: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error<List<Artist>>(e.message ?: "An error occurred"))
        }
    }
}