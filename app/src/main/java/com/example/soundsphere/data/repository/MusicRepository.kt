package com.example.soundsphere.data.repository

import com.example.soundsphere.data.models.*
import com.example.soundsphere.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getHomeData(): Flow<Resource<List<Modules>>>
    suspend fun getAlbums() : Flow<Resource<List<Album>>>
    suspend fun getArtists() : Flow<Resource<List<Artist>>>
}