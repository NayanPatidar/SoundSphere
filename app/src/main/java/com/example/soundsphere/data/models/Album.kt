// File: data/models/Album.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.ArtistMap
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Song

data class Album(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: String = "album",
    val header_desc: String,
    val language: String,
    val play_count: Int,
    val duration: Int,
    val year: Int,
    val list_count: Int,
    val list_type: String,
    val artist_map: ArtistMap,
    val song_count: Int,
    val label_url: String,
    val copyright_text: String,
    val is_dolby_content: Boolean,
    val songs: List<Song>,
    val modules: AlbumModules
)

data class AlbumModules(
    val recommend: AlbumModuleRecommend,
    val currently_trending: AlbumModuleCurrentlyTrending,
    val top_albums_from_same_year: AlbumModuleTopAlbums,
    val artists: AlbumModuleArtists
)

data class AlbumModuleRecommend(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val params: AlbumModuleRecommendParams
)

data class AlbumModuleRecommendParams(
    val id: String
)

data class AlbumModuleCurrentlyTrending(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val params: AlbumModuleCurrentlyTrendingParams
)

data class AlbumModuleCurrentlyTrendingParams(
    val type: String,
    val lang: String
)

data class AlbumModuleTopAlbums(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val params: AlbumModuleTopAlbumsParams
)

data class AlbumModuleTopAlbumsParams(
    val year: String,
    val lang: String
)

data class AlbumModuleArtists(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String
)
