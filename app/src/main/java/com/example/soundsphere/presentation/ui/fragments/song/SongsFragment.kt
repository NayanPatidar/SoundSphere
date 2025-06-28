package com.example.soundsphere.presentation.ui.fragments.song

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.soundsphere.R
import com.example.soundsphere.data.models.MusicItemPlayer
import com.example.soundsphere.data.repository.PlayerStateRepository
import com.example.soundsphere.databinding.FragmentSongBinding
import com.example.soundsphere.databinding.ItemSongHeaderBinding
import com.example.soundsphere.presentation.adapter.SongsAdapter
import com.example.soundsphere.presentation.viewmodel.SongViewModel
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject

class SongHeaderAdapter : RecyclerView.Adapter<SongHeaderAdapter.HeaderViewHolder>() {
    private var songData: JsonObject? = null

    fun submitSongData(data: JsonObject) {
        songData = data
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemSongHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        songData?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = 1

    class HeaderViewHolder(private val binding: ItemSongHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JsonObject) {
            Log.d("SongHeaderAdapter", "Binding data: $data")
            val songsArray = data.getAsJsonArray("songs")
            if (songsArray == null || songsArray.size() == 0) {
                Log.e("HeaderViewHolder", "Songs array is null or empty.")
                return
            }
            val firstSongObject = songsArray.get(0).asJsonObject
            val songName = firstSongObject.get("name")?.asString ?: "Unknown Song"
            binding.songTitle.text = songName
//            val artistNames = getArtistNames(firstSongObject)
//            binding..text = artistNames
            val imageUrl = getHighQualityImageUrl(firstSongObject)
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_music)
                .error(R.drawable.placeholder_music)
                .into(binding.songArtImageView)
            binding.playButton.setOnClickListener { /* Handle play all */ }
        }

        private fun getArtistNames(songJson: JsonObject): String {
            val primaryArtistsArray = songJson.getAsJsonObject("artist_map")
                ?.getAsJsonArray("primary_artists")
            val names = primaryArtistsArray?.mapNotNull { artistElement ->
                artistElement.asJsonObject?.get("name")?.asString
            }?.joinToString(", ")
            return if (names.isNullOrEmpty()) "Unknown Artist" else names
        }

        private fun getHighQualityImageUrl(songJson: JsonObject): String? {
            val imageUrlsArray = songJson.getAsJsonArray("image") ?: return null
            return imageUrlsArray.find { image ->
                image.isJsonObject && image.asJsonObject.get("quality")?.asString == "500x500"
            }?.asJsonObject?.get("link")?.asString
        }
    }
}

class SongFragment : Fragment() {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!

    private val songViewModel: SongViewModel by viewModels()
    private lateinit var songsAdapter: SongsAdapter
    private lateinit var headerAdapter: SongHeaderAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

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

    private fun getArtistNames(songJson: JsonObject): String {
        val artistMapObject = songJson.getAsJsonObject("artist_map")
        val primaryArtistsArray = artistMapObject?.getAsJsonArray("primary_artists")
        val artistNames = primaryArtistsArray
            ?.mapNotNull { artistElement ->
                artistElement.asJsonObject?.get("name")?.asString
            }
            ?.joinToString(", ")
        return if (artistNames.isNullOrEmpty()) {
            "Unknown Artist"
        } else {
            artistNames
        }
    }

    private fun handleSuccessResponse(jsonResponse: JsonObject?) {
        val songData = jsonResponse?.getAsJsonObject("data") ?: return

        headerAdapter.submitSongData(songData)

        val songsList = songData.getAsJsonArray("songs")?.map { it.asJsonObject }
        songsAdapter.submitList(songsList)
    }

    private fun setupRecyclerView() {
        headerAdapter = SongHeaderAdapter()
        songsAdapter = SongsAdapter { clickedSongJson, playlistJson ->
            val playlist = playlistJson.map { jsonToMusicItem(it) }
            val clickedSong = jsonToMusicItem(clickedSongJson)
            Log.d("ClickedSong", "Clicked Song: $clickedSong")
            Log.d("ClickedSong-Playlist", "Clicked Song: $playlist")
            PlayerStateRepository.playSong(clickedSong, playlist)
            Log.d("SongsFragment", "Playing '${clickedSong.name}'")
        }

        val concatAdapter = ConcatAdapter(headerAdapter, songsAdapter)
        binding.songsRecyclerView.adapter = concatAdapter
        binding.songsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


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
            subtitle = getArtistNames(songJson),
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
