// File: data/models/Models.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.Chart
import com.example.soundsphere.data.models.Radio
import com.example.soundsphere.data.models.Trending
import com.example.soundsphere.data.models.Quality
import com.example.soundsphere.data.models.Type
import com.example.soundsphere.data.models.Playlist
import com.example.soundsphere.data.models.Song

data class Module<T>(
    val title: String,
    val subtitle: String,
    val position: Int,
    val featured_text: String? = null,
    val source: String,
    val data: List<T>
)

data class Modules(
    val albums: Module<Any>, // Album or Song union type
    val artist_recos: Module<ArtistReco>,
    val charts: Module<Chart>,
    val city_mod: Module<CityMod>? = null,
    val discover: Module<Discover>,
    val mixes: Module<TagMix>,
    val playlists: Module<Playlist>,
    val radio: Module<Radio>,
    val trending: Module<Trending>,
    val global_config: GlobalConfig
)

data class ArtistReco(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: Type,
    val featured_station_type: Type,
    val query: String,
    val station_display_text: String
)

data class Discover(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: String = "channel",
    val badge: String,
    val is_featured: Boolean,
    val sub_type: Type,
    val tags: Map<String, List<String>>,
    val video_thumbnail: String,
    val video_url: String
)

data class CityMod(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: Type,
    val album_id: String? = null,
    val featured_station_type: String? = null,
    val query: String? = null
)

data class TagMix(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: Type,
    val first_name: String,
    val language: String,
    val last_name: String,
    val list_count: Int,
    val list_type: Type,
    val list: String,
    val play_count: Int,
    val year: Int
)

data class Promo(
    val explicit: Boolean,
    val id: String,
    val image: Quality,
    val url: String,
    val subtitle: String,
    val name: String,
    val type: Type,
    val editorial_language: String? = null,
    val language: String? = null,
    val list_count: Int? = null,
    val list_type: String? = null,
    val list: String? = null,
    val play_count: Int? = null,
    val release_year: Int? = null,
    val year: Int? = null
)

data class GlobalConfig(
    val random_songs_listid: GlobalConfigItem,
    val weekly_top_songs_listid: GlobalConfigItem
)

typealias GlobalConfigItem = Map<String, GlobalConfigItemLang>

data class GlobalConfigItemLang(
    val count: Int,
    val image: String,
    val listid: String,
    val title: String? = null
)
