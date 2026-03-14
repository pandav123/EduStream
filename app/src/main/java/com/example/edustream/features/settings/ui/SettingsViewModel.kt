package com.example.edustream.features.settings.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val downloadOverWifiOnly: Boolean = false,
    val videoQuality: String = "Auto"
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun toggleWifiOnly(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(downloadOverWifiOnly = enabled)
    }

    fun setVideoQuality(quality: String) {
        _uiState.value = _uiState.value.copy(videoQuality = quality)
    }
}
