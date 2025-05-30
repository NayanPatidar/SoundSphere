// File: data/models/Song.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.ArtistMap
import com.example.soundsphere.data.models.Lang
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Rights

data class SongObj(
    val songs: List<Song>,
    val modules: SongModules? = null
)

data class Song(
    val id: String,
    val name: String,
    val subtitle: String,
    val header_desc: String,
    val type: String = "song",
    val url: String,
    val image: Quality,
    val language: String,
    val year: Int,
    val play_count: Int,
    val explicit: Boolean,
    val list: String,
    val list_type: String,
    val list_count: Int,
    val music: String,
    val song: String? = null,
    val album: String,
    val album_id: String,
    val album_url: String,
    val label: String,
    val label_url: String,
    val origin: String,
    val is_dolby_content: Boolean,
    val `320kbps`: Boolean, // Backticks for property names with special characters
    val download_url: Quality,
    val duration: Int,
    val rights: Rights,
    val has_lyrics: Boolean,
    val lyrics_id: String? = null,
    val lyrics_snippet: String,
    val starred: Boolean,
    val copyright_text: String,
    val artist_map: ArtistMap,
    val release_date: String? = null,
    val vcode: String,
    val vlink: String,
    val triller_available: Boolean
)

data class SongModules(
    val recommend: SongModuleRecommend,
    val currently_trending: SongModuleCurrentlyTrending,
    val songs_by_same_artists: SongModuleSameArtists,
    val songs_by_same_actors: SongModuleSameActors,
    val artists: SongModuleArtists
)

data class SongModuleRecommend(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int,
    val params: SongModuleRecommendParams
)

data class SongModuleRecommendParams(
    val id: String,
    val lang: Lang
)

data class SongModuleCurrentlyTrending(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int,
    val params: SongModuleCurrentlyTrendingParams
)

data class SongModuleCurrentlyTrendingParams(
    val type: String,
    val lang: Lang
)

data class SongModuleSameArtists(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int,
    val params: SongModuleSameArtistsParams
)

data class SongModuleSameArtistsParams(
    val artist_id: String,
    val song_id: String,
    val lang: Lang
)

data class SongModuleSameActors(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int,
    val params: SongModuleSameActorsParams
)

data class SongModuleSameActorsParams(
    val actor_id: String,
    val song_id: String,
    val lang: Lang
)

data class SongModuleArtists(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int
)
