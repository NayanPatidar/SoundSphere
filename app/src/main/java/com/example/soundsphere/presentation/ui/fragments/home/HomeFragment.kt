// File: presentation/ui/fragments/home/HomeFragment.kt
package com.example.soundsphere.presentation.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.soundsphere.R
import com.example.soundsphere.data.models.MusicItem
import com.example.soundsphere.databinding.FragmentHomeBinding
import com.example.soundsphere.databinding.ItemModuleSectionBinding
import com.example.soundsphere.databinding.ItemMusicCardBinding
import com.example.soundsphere.presentation.viewmodel.HomeViewModel
import com.example.soundsphere.utils.Resource
import com.example.soundsphere.utils.getHref
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        addBottomPaddingForPlayer()
        homeViewModel.loadHomeData()
    }

    private fun setupObservers() {
        homeViewModel.homeData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    handleSuccessResponse(resource.data)
                }

                is Resource.Error -> {
                    Log.e("HomeFragment", "Error: ${resource.message}")
                }

                is Resource.Loading -> {
                    Log.d("HomeFragment", "Loading...")
                }
            }
        }
    }

    private fun addBottomPaddingForPlayer() {
        val playerBarHeight = resources.getDimensionPixelSize(R.dimen.mini_player_height)

        val recyclerView = binding.homeScrollView
        recyclerView.setPadding(
            recyclerView.paddingLeft,
            recyclerView.paddingTop,
            recyclerView.paddingRight,
            playerBarHeight
        )
        recyclerView.clipToPadding = false
    }

    private fun handleSuccessResponse(jsonResponse: JsonObject?) {
        val status = jsonResponse?.get("status")?.asString

        if (status == "Success") {
            val dataObject = jsonResponse.getAsJsonObject("data")
            processModules(dataObject)
        }
    }

    private fun processModules(dataObject: JsonObject) {
        binding.modulesContainer.removeAllViews()

        dataObject.entrySet()
            .filterNot { (key, section) -> shouldSkipSection(key, section.asJsonObject) }
            .sortedBy { (_, section) -> section.asJsonObject.get("position")?.asInt ?: 0 }
            .forEach { (key, sectionValue) ->
                val section = sectionValue.asJsonObject
                createModuleSection(key, section)
            }
    }

    private fun createModuleSection(key: String, section: JsonObject) {
        val title = section.get("title")?.asString ?: ""

        val moduleBinding = ItemModuleSectionBinding.inflate(
            LayoutInflater.from(requireContext()), binding.modulesContainer, false
        )
        moduleBinding.moduleTitle.text = title
        val dataArray = section.getAsJsonArray("data")
        processModuleItems(key, title, dataArray, moduleBinding)
        binding.modulesContainer.addView(moduleBinding.root)

        Log.d("HomeFragment", "Added module: $title")
    }

    private fun processModuleItems(
        key: String,
        title: String,
        dataArray: com.google.gson.JsonArray?,
        moduleBinding: ItemModuleSectionBinding
    ) {
        val horizontalScrollView = HorizontalScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isHorizontalScrollBarEnabled = false
        }

        val horizontalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setPadding(16, 0, 16, 0)
        }

        dataArray?.forEach { item ->
            val itemObj = item.asJsonObject

            val musicItem = MusicItem(
                name = itemObj.get("name")?.asString ?: "",
                subtitle = itemObj.get("subtitle")?.asString ?: "",
                imageUrl = fetchImageURL(itemObj.get("image")),
                clickUrl = itemObj.get("url")?.asString ?: "",
                type = itemObj.get("type")?.asString ?: ""
            )

            val cardBinding = ItemMusicCardBinding.inflate(
                LayoutInflater.from(requireContext()), null,
                false
            )

            cardBinding.itemName.text = musicItem.name
            cardBinding.itemSubtitle.text = musicItem.subtitle
            cardBinding.root.tag = musicItem


            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(16)
                marginStart = dpToPx(4)
            }

            cardBinding.root.layoutParams = layoutParams
            val imageUrl = fetchImageURL(itemObj.get("image"))
            Log.d("HomeFragment Image URL", "Image URL: $imageUrl")

            Glide.with(requireContext())
                .load(musicItem.imageUrl)
                .placeholder(R.drawable.placeholder_music)
                .error(R.drawable.placeholder_music)
                .into(cardBinding.albumArt)

            cardBinding.root.setOnClickListener { clickedView ->
                val retrievedItem = clickedView.tag as? MusicItem

                retrievedItem?.let { itemData ->

                    val newPath = getHref(itemData.clickUrl, itemData.type)
                    Log.d("Path", newPath)

                    Log.d("TypeSong", "Type: ${itemData.type}")
                    if (itemData.type.equals("album", ignoreCase = true)) {
                        val bundle = bundleOf(
                            "url" to getHref(itemData.clickUrl, itemData.type),
                            "type" to itemData.type
                        )

                        findNavController().navigate(R.id.action_home_to_album, bundle)
                    } else if (itemData.type.equals("song", ignoreCase = true)) {
                        val bundle = bundleOf(
                            "url" to getHref(itemData.clickUrl, itemData.type),
                            "type" to itemData.type
                        )

                        findNavController().navigate(R.id.action_home_to_song, bundle)
                    } else if (itemData.type.equals("playlist", ignoreCase = true)) {
                        Log.d("TypePlaylist", "Type: ${itemData.type}")
                        Log.d("PlaylistURL", "URL: ${getHref(itemData.clickUrl, itemData.type)}")
                        val bundle = bundleOf(
                            "url" to getHref(itemData.clickUrl, itemData.type),
                            "type" to itemData.type
                        )

                        findNavController().navigate(R.id.action_home_to_playlist, bundle)
                    }  else  {
                        Log.d(
                            "CardClick",
                            "Clicked on type '${itemData.type}', not navigating to album."
                        )
                    }

                }
            }

            if (cardBinding.root.parent != null) {
                (cardBinding.root.parent as ViewGroup).removeView(cardBinding.root)
            }

            horizontalLayout.addView(cardBinding.root)
        }

        horizontalScrollView.addView(horizontalLayout)

        moduleBinding.horizontalRecyclerView.visibility = View.GONE

        if (horizontalScrollView.parent != null) {
            (horizontalScrollView.parent as ViewGroup).removeView(horizontalScrollView)
        }

        (moduleBinding.root as LinearLayout).addView(horizontalScrollView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun fetchImageURL(image: JsonElement?): String {
        Log.d("fetchImageURL", "Input: $image")

        return when {
            image == null -> {
                Log.d("fetchImageURL", "Image is null")
                ""
            }

            image.isJsonPrimitive && image.asJsonPrimitive.isString -> {
                val url = image.asString
                Log.d("fetchImageURL", "Direct string URL: $url")
                url
            }

            image.isJsonArray && image.asJsonArray.size() > 2 -> {
                val url = image.asJsonArray[2].asJsonObject.get("link")?.asString ?: ""
                url
            }

            else -> {
                Log.d("fetchImageURL", "Unknown format or empty array: $image")
                ""
            }
        }
    }


    private fun shouldSkipSection(key: String, section: JsonObject): Boolean {
        return section.has("random_songs_listid") || key == "artist_recos" || key == "city_mod" || key == "mixes" || key == "discover" || key == "promo7" || key == "promo3" || key == "promo1" || key == "radio"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
