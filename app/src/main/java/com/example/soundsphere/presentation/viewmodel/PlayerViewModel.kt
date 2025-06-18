package com.example.soundsphere.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.soundsphere.data.repository.PlayerStateRepository

class PlayerViewModel : ViewModel() {
    val playerState = PlayerStateRepository.playerState
}
