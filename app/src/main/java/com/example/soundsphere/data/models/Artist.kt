// File: data/models/Artist.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import Album
import Quality
import Playlist
import Song

data class Artist(
    val id: String,
    val name: String,
    val subtitle: String,
    val image: Quality,
    val follower_count: Int,
    val type: String = "artist",
    val is_verified: Boolean,
    val dominant_language: String,
    val dominant_type: String,
    val top_songs: List<Song>,
    val top_albums: List<Album>,
    val dedicated_artist_playlist: List<Playlist>,
    val featured_artist_playlist: List<Playlist>,
    val singles: List<ArtistSong>,
    val latest_release: List<ArtistSong>,
    val similar_artists: List<SimilarArtist>,
    val is_radio_present: Boolean,
    val bio: List<ArtistBio>,
    val dob: String,
    val fb: String,
    val twitter: String,
    val wiki: String,
    val urls: Urls,
    val available_languages: List<String>,
    val fan_count: Int,
    val is_followed: Boolean,
    val modules: ArtistModules
)

data class ArtistBio(
    val title: String,
    val text: String,
    val sequence: Int
)

data class ArtistModules(
    val top_songs: Module,
    val latest_release: Module,
    val top_albums: Module,
    val dedicated_artist_playlist: Module,
    val featured_artist_playlist: Module,
    val singles: Module,
    val similar_artists: Module
)

data class SimilarArtist(
    val id: String,
    val name: String,
    val roles: Map<String, String>,
    val aka: String,
    val fb: String,
    val twitter: String,
    val wiki: String,
    val similar: List<SimilarArtistItem>,
    val dob: String,
    val image: Quality,
    val search_keywords: String,
    val primary_artist_id: String,
    val languages: Map<String, String>,
    val url: String,
    val type: String = "artist",
    val is_radio_present: Boolean,
    val dominant_type: String
)

data class SimilarArtistItem(
    val id: String,
    val name: String
)

data class ArtistMap(
    val primary_artists: List<ArtistMini>,
    val featured_artists: List<ArtistMini>,
    val artists: List<ArtistMini>
)

data class ArtistMini(
    val id: String,
    val image: Quality,
    val url: String,
    val name: String,
    val type: String = "artist",
    val role: String
)

data class ArtistSong(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String,
    val url: String,
    val image: Quality,
    val language: String,
    val year: Int,
    val play_count: Int,
    val explicit: Boolean,
    val list_count: Int,
    val list_type: String,
    val music: String, // Assuming this is a String, adjust based on actual Song type
    val artist_map: ArtistMap,
    val query: String,
    val text: String,
    val song_count: Int
)

data class ArtistSongsOrAlbums(
    val id: String,
    val name: String,
    val image: Quality,
    val follower_count: Int,
    val type: String = "artist",
    val is_verified: Boolean,
    val dominant_language: String,
    val dominant_type: String,
    val top_songs: ArtistTopSongs,
    val top_albums: ArtistTopAlbums
)

data class Module(
    val title: String,
    val subtitle: String,
    val source: String,
    val position: Int
)

data class Urls(
    val albums: String,
    val bio: String,
    val comments: String,
    val songs: String
)

data class ArtistTopSongsOrAlbums<T>(
    val total: Int,
    val last_page: Boolean,
    val songs: List<T>,
    val albums: List<T>
)

// Type aliases for specific use cases
typealias ArtistTopSongs = ArtistTopSongsOrAlbums<Song>
typealias ArtistTopAlbums = ArtistTopSongsOrAlbums<Album>
