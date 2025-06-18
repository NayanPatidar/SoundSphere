// File: data/models/GetModels.kt
package com.example.soundsphere.data.models

import com.example.soundsphere.data.models.Album
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Type
import com.example.soundsphere.data.models.Playlist
import com.example.soundsphere.data.models.Song

data class A_2<T>(
    val count: Int,
    val last_page: Boolean,
    val data: List<T>
)

data class B(
    val id: String,
    val title: String,
    val action: String
)

data class FooterDetails(
    val playlist: List<B>,
    val artist: List<B>,
    val album: List<B>,
    val actor: List<B>
)

data class Lyrics(
    val lyrics: String,
    val script_tracking_url: String,
    val lyrics_copyright: String,
    val snippet: String
)

// Union type for Trending - using sealed class
sealed class TrendingItem {
    data class AlbumItem(val album: Album) : TrendingItem()
    data class SongItem(val song: Song) : TrendingItem()
    data class PlaylistItem(val playlist: Playlist) : TrendingItem()
}

typealias Trending = List<TrendingItem>

typealias FeaturedPlaylists = A_2<Playlist>

data class Chart(
    val id: String,
    val name: String,
    val subtitle: String? = null,
    val type: String = "playlist",
    val image: Quality,
    val url: String,
    val explicit: Boolean? = null,
    val count: Int? = null,
    val first_name: String? = null,
    val language: String? = null,
    val listname: String? = null
)

data class TopShows(
    val count: Int,
    val last_page: Boolean,
    val data: List<TopShow>,
    val trending_podcasts: TrendingPodcasts
)

data class TrendingPodcasts(
    val title: String,
    val subtitle: String,
    val source: String,
    val data: List<TrendingPodcastItem>
)

data class TrendingPodcastItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String = "show",
    val image: Quality,
    val url: String,
    val explicit: Boolean
)

data class TopShow(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String = "show",
    val image: Quality,
    val url: String,
    val explicit: Boolean,
    val season_number: Int,
    val release_date: String,
    val badge: String
)

data class TopArtist(
    val id: String,
    val name: String,
    val image: Quality,
    val url: String,
    val follower_count: Int,
    val is_followed: Boolean
)

typealias TopArtists = List<TopArtist>

// Union type for TopAlbum - using sealed class
sealed class TopAlbumItem {
    data class SongItem(val song: Song) : TopAlbumItem()
    data class AlbumItem(val album: Album) : TopAlbumItem()
}

typealias TopAlbum = A_2<TopAlbumItem>

data class Radio(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String = "radio_station",
    val image: Quality,
    val url: String,
    val explicit: Boolean,
    val color: String? = null,
    val description: String? = null,
    val featured_station_type: Type,
    val language: String,
    val query: String? = null,
    val station_display_text: String
)

data class Mix(
    val id: String,
    val name: String,
    val subtitle: String,
    val header_desc: String,
    val type: String = "mix",
    val url: String,
    val image: Quality,
    val language: String,
    val year: Int,
    val play_count: Int,
    val explicit: Boolean,
    val list_count: Int,
    val list_type: String,
    val songs: List<Song>,
    val user_id: String,
    val last_updated: String,
    val username: String,
    val firstname: String,
    val lastname: String,
    val is_followed: Boolean,
    val share: Int
)

data class Label(
    val id: String,
    val name: String,
    val image: Quality,
    val type: String = "label",
    val top_songs: LabelTopSongs,
    val top_albums: LabelTopAlbums,
    val urls: LabelUrls,
    val available_languages: List<String>
)

data class LabelTopSongs(
    val songs: List<Song>,
    val total: Int
)

data class LabelTopAlbums(
    val albums: List<Album>,
    val total: Int
)

data class LabelUrls(
    val albums: String,
    val songs: String
)

data class MegaMenu(
    val top_artists: List<MegaMenuItem>,
    val top_playlists: List<MegaMenuItem>,
    val new_releases: List<MegaMenuItem>
)

data class MegaMenuItem(
    val name: String,
    val url: String
)
