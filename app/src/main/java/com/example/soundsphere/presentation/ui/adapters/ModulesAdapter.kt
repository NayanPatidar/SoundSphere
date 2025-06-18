package com.example.soundsphere.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soundsphere.data.models.Modules
import com.example.soundsphere.databinding.ItemModuleBinding

class ModulesAdapter : RecyclerView.Adapter<ModulesAdapter.ModuleViewHolder>() {

    private var modulesList = listOf<Modules>()

    fun updateModules(newModules: List<Modules>) {
        modulesList = newModules
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modulesList[position])
    }

    override fun getItemCount() = modulesList.size

    class ModuleViewHolder(private val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Modules) {
            binding.titleText.text = "Music Home"
            binding.subtitleText.text = "Albums, Playlists, Charts & More"
            binding.root.setOnClickListener {
            }
        }
    }
}
