// File: data/models/Search.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.Album
import com.example.soundsphere.data.models.ArtistMap
import com.example.soundsphere.data.models.ArtistMini
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Type
import com.example.soundsphere.data.models.Playlist
import com.example.soundsphere.data.models.Song

data class A<T>(
    val position: Int,
    val data: List<T>
)

data class Search<T>(
    val total: Int,
    val start: Int,
    val results: List<T>
)

data class TopSearch(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: Type,
    val image: Quality,
    val url: String,
    val explicit: Boolean,
    val album: String,
    val artist_map: List<ArtistMap>
)

data class AllSearch(
    val albums: A<AllSearchAlbum>,
    val songs: A<AllSearchSong>,
    val playlists: A<AllSearchPlaylist>,
    val artists: A<AllSearchArtist>,
    val top_query: A<AllSearchTopQuery>,
    val shows: A<AllSearchShow>
)

data class AllSearchAlbum(
    val id: String,
    val name: String,
    val subtitle: String,
    val image: Quality,
    val music: String,
    val url: String,
    val type: String = "album",
    val position: Int,
    val year: Int,
    val is_movie: Boolean,
    val language: String,
    val song_pids: String
)

data class AllSearchSong(
    val id: String,
    val name: String,
    val subtitle: String,
    val image: Quality,
    val album: String,
    val url: String,
    val type: String = "song",
    val position: Int,
    val primary_artists: String,
    val singers: String,
    val language: String
)

data class AllSearchPlaylist(
    val id: String,
    val name: String,
    val subtitle: String,
    val image: Quality,
    val extra: String,
    val url: String,
    val language: String,
    val type: String = "playlist",
    val position: Int,
    val firstname: String,
    val lastname: String,
    val artist_name: String,
    val entity_type: String,
    val entity_sub_type: String,
    val is_dolby_content: Boolean,
    val sub_types: String
)

data class AllSearchArtist(
    val id: String,
    val name: String,
    val image: Quality,
    val extra: String,
    val url: String,
    val type: String = "artist",
    val subtitle: String,
    val entity: Int,
    val position: Int
)

data class AllSearchTopQuery(
    val id: String,
    val name: String,
    val subtitle: String,
    val image: Quality,
    val album: String,
    val url: String,
    val type: Type,
    val position: Int,
    val primary_artists: String,
    val singers: String,
    val language: String
)

data class AllSearchShow(
    val id: String,
    val name: String,
    val image: Quality,
    val type: String = "show",
    val season_number: Int,
    val subtitle: String,
    val url: String,
    val position: Int
)

typealias SongSearch = Search<Song>

typealias AlbumSearch = Search<Album>

typealias PlaylistSearch = Search<Playlist>

data class ArtistSearchItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String = "artist",
    val ctr: Int,
    val entity: Int,
    val image: Quality,
    val role: String,
    val url: String,
    val is_radio_present: Boolean,
    val is_followed: Boolean
)

typealias ArtistSearch = Search<ArtistSearchItem>

data class PodcastSearchItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String = "show",
    val image: Quality,
    val partner_name: String,
    val label_name: String,
    val explicit: Boolean,
    val season: Int,
    val artists: List<ArtistMini>,
    val featured_artists: List<ArtistMini>,
    val primary_artists: List<ArtistMini>,
    val url: String
)

typealias PodcastSearch = Search<PodcastSearchItem>

// Union type for SearchReturnType - using sealed class
sealed class SearchReturnType {
    data class SongSearchResult(val search: SongSearch) : SearchReturnType()
    data class AlbumSearchResult(val search: AlbumSearch) : SearchReturnType()
    data class PlaylistSearchResult(val search: PlaylistSearch) : SearchReturnType()
    data class ArtistSearchResult(val search: ArtistSearch) : SearchReturnType()
    data class PodcastSearchResult(val search: PodcastSearch) : SearchReturnType()
}
