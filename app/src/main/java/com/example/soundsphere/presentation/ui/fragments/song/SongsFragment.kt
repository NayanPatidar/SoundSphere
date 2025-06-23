package com.example.soundsphere.presentation.ui.fragments.song

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.soundsphere.R
import com.example.soundsphere.data.models.MusicItemPlayer
import com.example.soundsphere.data.repository.PlayerStateRepository
import com.example.soundsphere.databinding.FragmentSongBinding
import com.example.soundsphere.presentation.viewmodel.SongViewModel
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject

class SongFragment : Fragment() {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!

    private val songViewModel: SongViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songUrl = arguments?.getString("url") ?: ""
        val songId = songUrl.substringAfterLast('/')
        Log.d("SongFragment", "Song ID: $songId")

        if (songUrl.isNotBlank()) {
            setupObservers()
            songViewModel.loadSongData(songId)
        } else {
            Log.e("SongFragment", "Song ID is missing or invalid.")
        }
    }

    private fun setupObservers() {
        songViewModel.songData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleSuccessResponse(resource.data)
                is Resource.Error -> Log.e("SongFragment", "Error loading song: ${resource.message}")
                is Resource.Loading -> Log.d("SongFragment", "Loading song details...")
            }
        }
    }

    private fun handleSuccessResponse(jsonResponse: JsonObject?) {
        // Assuming the response for a single song is structured with a "data" object containing a list
        Log.d("SongFragment", "$jsonResponse")
        val songData = jsonResponse?.getAsJsonObject("data")?.getAsJsonObject("songs") ?: return
        Log.d("SongFragment", "$songData")
        Log.d("SongFragment", "$songData.get(\"name\")")
        Log.d("SongFragment", "$songData.get(\"primaryArtists\")")


//        // Populate UI
//        binding.songTitleTextView.text = songData.get("name")?.asString ?: "Unknown Title"
//        binding.songArtistTextView.text = songData.get("primaryArtists")?.asString ?: "Unknown Artist"

//        val imageUrl = songData.getAsJsonArray("image")?.lastOrNull()?.asJsonObject?.get("link")?.asString
//        Glide.with(this)
//            .load(imageUrl)
//            .placeholder(R.drawable.placeholder_music)
//            .into(binding.songArtImageView)

        // Set up play button
//        binding.playSongButton.setOnClickListener {
//            val musicItem = jsonToMusicItem(songData)
//            // For a single song screen, the playlist contains only that song
//            PlayerStateRepository.playSong(musicItem, listOf(musicItem))
//            Log.d("SongFragment", "Playing '${musicItem.name}'")
//        }
    }

    // This utility function is identical to the one in your AlbumFragment.
    // Consider moving it to a shared utility file to avoid duplication.
    private fun jsonToMusicItem(songJson: JsonObject): MusicItemPlayer {
        val downloadUrls = songJson.get("download_url")?.asJsonArray
        val highQualityUrl = downloadUrls?.find {
            it.asJsonObject.get("quality")?.asString == "320kbps"
        }?.asJsonObject?.get("link")?.asString
        val durationInSeconds = songJson.get("duration")?.asInt ?: 0
        val durationInMillis = durationInSeconds * 1000

        val imageUrls = songJson.get("image")?.asJsonArray
        val highQualityImageUrl = imageUrls?.find {
            it.asJsonObject.get("quality")?.asString == "500x500"
        }?.asJsonObject?.get("link")?.asString

        return MusicItemPlayer(
            id = songJson.get("id")?.asString ?: "",
            name = songJson.get("name")?.asString ?: "Unknown Song",
            type = songJson.get("type")?.asString ?: "song",
            subtitle = songJson.get("primaryArtists")?.asString ?: "Unknown Artist",
            imageUrl = highQualityImageUrl.toString(),
            songUrl = highQualityUrl,
            duration = durationInMillis
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
