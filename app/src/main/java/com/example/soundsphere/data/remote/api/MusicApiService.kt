package com.example.soundsphere.data.remote.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("/modules")
    suspend fun getHomeData(): Response<JsonObject>

    @GET("/album")
    suspend fun getAlbumDetails(@Query("token") albumId: String): Response<JsonObject>

    @GET("/artist")
    suspend fun getArtists(): Response<JsonObject>

    @GET("/song")
    suspend fun getSongDetails(@Query("token") songId: String): Response<JsonObject>

    @GET("/playlist")
    suspend fun getPlaylistDetails(@Query("token") playlistId: String): Response<JsonObject>
}