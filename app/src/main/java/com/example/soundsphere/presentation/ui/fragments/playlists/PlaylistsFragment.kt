package com.example.soundsphere.presentation.ui.fragments.playlist

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
import com.example.soundsphere.databinding.FragmentPlaylistPageBinding
import com.example.soundsphere.databinding.ItemPlaylistHeaderBinding
import com.example.soundsphere.presentation.adapter.SongsAdapter
import com.example.soundsphere.presentation.viewmodel.PlaylistPageViewModel
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject

class PlaylistHeaderAdapter : RecyclerView.Adapter<PlaylistHeaderAdapter.HeaderViewHolder>() {
    private var playlistData: JsonObject? = null

    fun submitPlaylistData(data: JsonObject) {
        playlistData = data
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemPlaylistHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        playlistData?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = 1

    class HeaderViewHolder(private val binding: ItemPlaylistHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JsonObject) {
            Log.d("PlaylistHeaderAdapter", "Binding data: $data")
            binding.playlistTitle.text = data.get("name")?.asString ?: "Unknown Playlist"
            val imageUrl =
                data.get("image")?.asString
            Log.d("PlaylistHeaderAdapter", "Image URL: $imageUrl")
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_music)
                .error(R.drawable.artist)
                .into(binding.playlistArtImageView)
            binding.playButton.setOnClickListener { /* Handle play all */ }
        }
    }
}

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistPageBinding? = null
    private val binding get() = _binding!!

    private val playlistViewModel: PlaylistPageViewModel by viewModels()
    private lateinit var songsAdapter: SongsAdapter
    private lateinit var headerAdapter: PlaylistHeaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val playlistUrl = arguments?.getString("url") ?: ""
        val playlistId = playlistUrl.substringAfterLast('/')
        Log.d("PlaylistFragment", "Playlist ID: $playlistId")

        if (playlistId.isNotBlank()) {
            setupObservers()
            playlistViewModel.loadPlaylistData(playlistId)
        } else {
            Log.e("PlaylistFragment", "Failed to extract a valid ID from the path.")
        }
    }

    private fun setupObservers() {
        playlistViewModel.playlistData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleSuccessResponse(resource.data)
                is Resource.Error -> Log.e("PlaylistFragment", "Error: ${resource.message}")
                is Resource.Loading -> Log.d("PlaylistFragment", "Loading...")
            }
        }
    }

    private fun handleSuccessResponse(jsonResponse: JsonObject?) {
        val playlistData = jsonResponse?.getAsJsonObject("data") ?: return
        headerAdapter.submitPlaylistData(playlistData)

        val songsList = playlistData.getAsJsonArray("songs")?.map { it.asJsonObject }
        songsAdapter.submitList(songsList)
    }

    private fun setupRecyclerView() {
        headerAdapter = PlaylistHeaderAdapter()
        songsAdapter = SongsAdapter { clickedSongJson, playlistJson ->
            val playlist = playlistJson.map { jsonToMusicItem(it) }
            val clickedSong = jsonToMusicItem(clickedSongJson)
            Log.d("ClickedSong", "Clicked Song: $clickedSong")
            Log.d("ClickedSong-Playlist", "Clicked Song: $playlist")
            PlayerStateRepository.playSong(clickedSong, playlist)
            Log.d("PlaylistFragment", "Playing '${clickedSong.name}'")
        }

        val concatAdapter = ConcatAdapter(headerAdapter, songsAdapter)
        binding.songsRecyclerView.adapter = concatAdapter
        binding.songsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    private fun jsonToMusicItem(songJson: JsonObject): MusicItemPlayer {
        val imageUrls = songJson.get("image")?.asString
        Log.d("PlaylistImage", imageUrls.toString())

        return MusicItemPlayer(
            id = songJson.get("id")?.asString ?: "",
            name = songJson.get("name")?.asString ?: "Unknown Song",
            type = songJson.get("type")?.asString ?: "song",
            subtitle = "None",
            imageUrl = imageUrls.toString(),
            songUrl = "",
            duration = 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
