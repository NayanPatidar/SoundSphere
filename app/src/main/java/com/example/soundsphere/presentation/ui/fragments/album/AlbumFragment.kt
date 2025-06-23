package com.example.soundsphere.presentation.ui.fragments.album

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
import com.example.soundsphere.databinding.FragmentAlbumBinding
import com.example.soundsphere.databinding.ItemAlbumHeaderBinding
import com.example.soundsphere.presentation.adapter.SongsAdapter
import com.example.soundsphere.presentation.viewmodel.AlbumViewModel
import com.example.soundsphere.utils.Resource
import com.google.gson.JsonObject

class AlbumHeaderAdapter : RecyclerView.Adapter<AlbumHeaderAdapter.HeaderViewHolder>() {
    private var albumData: JsonObject? = null

    fun submitAlbumData(data: JsonObject) {
        albumData = data
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemAlbumHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        albumData?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = 1

    class HeaderViewHolder(private val binding: ItemAlbumHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JsonObject) {
            binding.albumTitle.text = data.get("name")?.asString ?: "Unknown Album"
            val imageUrl =
                data.getAsJsonArray("image")?.lastOrNull()?.asJsonObject?.get("link")?.asString
            Glide.with(itemView.context).load(imageUrl).placeholder(R.drawable.placeholder_music)
                .into(binding.albumArtImageView)
            binding.playButton.setOnClickListener { /* Handle play all */ }
        }
    }
}


class AlbumFragment : Fragment() {
    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val albumViewModel: AlbumViewModel by viewModels()
    private lateinit var songsAdapter: SongsAdapter
    private lateinit var headerAdapter: AlbumHeaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val albumUrl = arguments?.getString("url") ?: ""
        val albumId = albumUrl.substringAfterLast('/')
        Log.d("AlbumFragment", "Album ID: $albumId")

        if (albumId.isNotBlank()) {
            setupObservers()
            albumViewModel.loadAlbumData(albumId)
        } else {
            Log.e("AlbumFragment", "Failed to extract a valid ID from the path.")
        }
    }

    private fun setupObservers() {
        albumViewModel.albumData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleSuccessResponse(resource.data)
                is Resource.Error -> Log.e("AlbumFragment", "Error: ${resource.message}")
                is Resource.Loading -> Log.d("AlbumFragment", "Loading...")
            }
        }
    }

    private fun handleSuccessResponse(jsonResponse: JsonObject?) {
        val albumData = jsonResponse?.getAsJsonObject("data") ?: return

        headerAdapter.submitAlbumData(albumData)

        val songsList = albumData.getAsJsonArray("songs")?.map { it.asJsonObject }
        songsAdapter.submitList(songsList)
    }

    private fun setupRecyclerView() {
        headerAdapter = AlbumHeaderAdapter()
        songsAdapter = SongsAdapter { clickedSongJson, playlistJson ->
            val playlist = playlistJson.map { jsonToMusicItem(it) }
            val clickedSong = jsonToMusicItem(clickedSongJson)
            Log.d("ClickedSong", "Clicked Song: $clickedSong")
            Log.d("ClickedSong-Playlist", "Clicked Song: $playlist")
            PlayerStateRepository.playSong(clickedSong, playlist)
            Log.d("AlbumFragment", "Playing '${clickedSong.name}'")
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

        Log.d("AlbumFragment", "High Quality URL: $highQualityUrl")

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
