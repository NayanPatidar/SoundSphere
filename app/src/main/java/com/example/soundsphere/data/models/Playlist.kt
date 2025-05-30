// File: data/models/Playlist.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.ArtistMini
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Song

data class Playlist(
    val id: String,
    val name: String,
    val subtitle: String,
    val header_desc: String,
    val type: String = "playlist",
    val url: String,
    val image: Quality,
    val language: String,
    val year: Int? = null,
    val play_count: Int? = null,
    val explicit: Boolean,
    val list_count: Int? = null,
    val list_type: String,
    val user_id: String,
    val is_dolby_content: Boolean,
    val last_updated: String? = null,
    val username: String,
    val firstname: String,
    val lastname: String,
    val follower_count: Int? = null,
    val fan_count: Int? = null,
    val share: Int? = null,
    val video_count: Int? = null,
    val artists: List<ArtistMini>? = null,
    val subtitle_desc: List<String>,
    val songs: List<Song>? = null,
    val modules: PlaylistModules? = null
)

data class PlaylistModules(
    val related_playlist: PlaylistModuleRelated,
    val currently_trending_playlists: PlaylistModuleCurrentlyTrending,
    val artists: PlaylistModuleArtists
)

data class PlaylistModuleRelated(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val params: PlaylistModuleRelatedParams
)

data class PlaylistModuleRelatedParams(
    val id: String
)

data class PlaylistModuleCurrentlyTrending(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val params: PlaylistModuleCurrentlyTrendingParams
)

data class PlaylistModuleCurrentlyTrendingParams(
    val type: String,
    val lang: String
)

data class PlaylistModuleArtists(
    val source: String,
    val position: Int,
    val title: String,
    val subtitle: String
)
