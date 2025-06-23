package com.example.soundsphere.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soundsphere.R
import com.example.soundsphere.databinding.ItemSongBinding
import com.google.gson.JsonObject
import java.text.NumberFormat
import java.util.Locale

class SongsAdapter(
    private val onItemClicked: (song: JsonObject, playlist: List<JsonObject>) -> Unit
) : ListAdapter<JsonObject, SongsAdapter.SongViewHolder>(SongViewHolder.SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songObject = getItem(position)
        holder.bind(songObject, position + 1, currentList, onItemClicked)
    }

    class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            songObject: JsonObject, number: Int, playlist: List<JsonObject>,
            onItemClicked: (song: JsonObject, playlist: List<JsonObject>) -> Unit
        ) {
            val title = songObject.get("name")?.asString ?: "Unknown Title"
            val playCount: Long = try {
                songObject.get("play_count")?.asLong ?: 0L
            } catch (e: NumberFormatException) {
                0L
            }
            val formattedPlayCount = NumberFormat.getNumberInstance(Locale.US).format(playCount)
            binding.songNumber.text = number.toString()
            binding.songTitle.text = title
            binding.songPlayCount.text = formattedPlayCount

            val context = binding.root.context
            binding.root.contentDescription = context.getString(
                R.string.song_item_description,
                number,
                title,
                formattedPlayCount
            )

            binding.root.setOnClickListener {
                onItemClicked(songObject, playlist)
            }
        }

        class SongDiffCallback : DiffUtil.ItemCallback<JsonObject>() {
            override fun areItemsTheSame(oldItem: JsonObject, newItem: JsonObject): Boolean {
                return oldItem.get("id")?.asString == newItem.get("id")?.asString
            }

            override fun areContentsTheSame(oldItem: JsonObject, newItem: JsonObject): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        }
    }
}
