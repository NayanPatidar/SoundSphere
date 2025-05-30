package com.example.soundsphere.data.remote.api

import com.example.soundsphere.data.models.Album
import com.example.soundsphere.data.models.Artist
import com.example.soundsphere.data.models.Modules
import retrofit2.Response
import retrofit2.http.GET

interface MusicApiService {
    @GET("/modules")
    suspend fun getHomeData(): Response<List<Modules>>

    @GET("/album")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("/artist")
    suspend fun getArtists(): Response<List<Artist>>
}