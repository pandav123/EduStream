package com.example.edustream.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.dao.CourseDao
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.features.player.data.local.dao.PlaybackProgressDao
import com.example.edustream.features.player.data.local.entities.PlaybackProgressEntity
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val courseDao: CourseDao,
    private val playbackProgressDao: PlaybackProgressDao
) : ViewModel() {

    private val _featuredCourses = MutableStateFlow<UiState<List<CourseEntity>>>(UiState.Loading)
    val featuredCourses = _featuredCourses.asStateFlow()

    private val _inProgressCourses = MutableStateFlow<List<PlaybackProgressEntity>>(emptyList())
    val inProgressCourses = _inProgressCourses.asStateFlow()

    init {
        observeInProgressCourses()
        observeFeaturedCourses()
        loadHomeData()
    }

    private fun observeFeaturedCourses() {
        viewModelScope.launch {
            courseDao.getLatestCourses().collectLatest { courses ->
                if (courses.isNotEmpty()) {
                    _featuredCourses.value = UiState.Success(courses)
                }
            }
        }
    }

    private fun observeInProgressCourses() {
        viewModelScope.launch {
            playbackProgressDao.getInProgressCourses().collectLatest {
                _inProgressCourses.value = it
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                repository.refreshCourses()
                // If the list is still empty after refresh, we might want to show an empty state
                val currentCourses = courseDao.getLatestCourses().first()
                if (currentCourses.isEmpty() && _featuredCourses.value is UiState.Loading) {
                    _featuredCourses.value = UiState.Success(emptyList())
                }
            } catch (e: Exception) {
                if (_featuredCourses.value is UiState.Loading) {
                    _featuredCourses.value = UiState.Error(e.message ?: "Failed to load courses")
                }
            }
        }
    }
}
