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
        loadHomeData()
        observeInProgressCourses()
        observeFeaturedCourses()
    }

    private fun observeFeaturedCourses() {
        viewModelScope.launch {
            repository.refreshCourses()
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
            _featuredCourses.value = UiState.Loading
            repository.refreshCourses()
            _featuredCourses.value = UiState.Success(listOf(
                CourseEntity("1", "Android Development", "Learn Compose", "instr_1", "https://developer.android.com/static/images/courses/android-basics-compose.png", "", 4999.0, 9999.0, "Development", 4.8f, 1200, 50, 36000, false, System.currentTimeMillis()),
                CourseEntity("2", "UI/UX Design", "Design beautiful apps", "instr_2", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/27cc6a105243111.5f749a099092b.png", "", 2999.0, null, "Design", 4.5f, 800, 30, 25000, true, System.currentTimeMillis())
            ))
        }
    }
}
