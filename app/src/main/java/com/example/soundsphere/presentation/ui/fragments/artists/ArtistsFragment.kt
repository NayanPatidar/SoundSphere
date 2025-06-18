// File: presentation/ui/fragments/artists/ArtistsFragment.kt
package com.example.soundsphere.presentation.ui.fragments.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.soundsphere.databinding.FragmentArtistsBinding

class ArtistsFragment : Fragment() {

    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
