package com.example.soundsphere.data.repository

import com.example.soundsphere.data.models.*
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getHomeData(): Flow<Resource<JsonObject>>
    suspend fun getAlbumDetails(albumId: String): Flow<Resource<JsonObject>>
    suspend fun getArtists() : Flow<Resource<JsonObject>>
}