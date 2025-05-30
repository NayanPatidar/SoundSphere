// File: data/models/Misc.kt
package com.example.soundsphere.data.models

// Imported types - we'll need these too
import com.example.soundsphere.data.models.ArtistMini

enum class Type {
    ARTIST,
    ALBUM,
    PLAYLIST,
    RADIO,
    RADIO_STATION,
    SONG,
    CHANNEL,
    MIX,
    SHOW,
    EPISODE,
    SEASON,
    LABEL
}

// Union type for Quality - using sealed class
sealed class Quality {
    data class StringQuality(val quality: String) : Quality()
    data class QualityArray(val qualities: List<QualityItem>) : Quality()
}

data class QualityItem(
    val quality: String,
    val link: String
)

enum class ImageQuality {
    LOW,
    MEDIUM,
    HIGH
}

enum class StreamQuality {
    POOR,
    LOW,
    MEDIUM,
    HIGH,
    EXCELLENT
}

data class Rights(
    val code: Any?, // unknown type converted to Any?
    val cacheable: Any?,
    val delete_cached_object: Any?,
    val reason: Any?
)

enum class Lang {
    HINDI,
    ENGLISH,
    PUNJABI,
    TAMIL,
    TELUGU,
    MARATHI,
    GUJARATI,
    BENGALI,
    KANNADA,
    BHOJPURI,
    MALAYALAM,
    URDU,
    HARYANVI,
    RAJASTHANI,
    ODIA,
    ASSAMESE
}

enum class Category {
    LATEST,
    ALPHABETICAL,
    POPULARITY
}

enum class Sort {
    ASC,
    DESC
}

data class Queue(
    val id: String,
    val name: String,
    val subtitle: String,
    val url: String,
    val type: QueueType,
    val image: Quality,
    val artists: List<ArtistMini>,
    val download_url: Quality,
    val duration: Int
)

enum class QueueType {
    SONG,
    EPISODE
}
