package com.example.edustream.features.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.features.player.data.local.dao.PlaybackProgressDao
import com.example.edustream.features.player.data.local.entities.PlaybackProgressEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val repository: CourseRepository,
    private val playbackProgressDao: PlaybackProgressDao,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val lectureId: String = checkNotNull(savedStateHandle["lectureId"])
    
    val player = ExoPlayer.Builder(application).build()

    private val _currentLecture = MutableStateFlow<LectureEntity?>(null)
    val currentLecture = _currentLecture.asStateFlow()

    private var progressTrackerJob: Job? = null

    init {
        loadLecture()
        startTrackingProgress()
    }

    private fun loadLecture() {
        viewModelScope.launch {
            // Restore progress
            val savedProgress = playbackProgressDao.getProgressByLecture(lectureId).firstOrNull()
            savedProgress?.let {
                player.seekTo(it.lastPosition)
            }
        }
    }

    private fun startTrackingProgress() {
        progressTrackerJob?.cancel()
        progressTrackerJob = viewModelScope.launch {
            while (true) {
                delay(10000) // Save every 10 seconds
                saveProgress()
            }
        }
    }

    private suspend fun saveProgress() {
        val currentPos = player.currentPosition
        val duration = player.duration
        if (duration > 0) {
            val progress = PlaybackProgressEntity(
                lectureId = lectureId,
                courseId = "", // Should be passed or fetched
                userId = "current_user", // Should come from Auth
                lastPosition = currentPos,
                totalDuration = duration,
                lastUpdated = System.currentTimeMillis(),
                isCompleted = currentPos >= (duration * 0.9)
            )
            playbackProgressDao.upsertProgress(progress)
        }
    }

    fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            saveProgress()
            player.release()
        }
    }
}
