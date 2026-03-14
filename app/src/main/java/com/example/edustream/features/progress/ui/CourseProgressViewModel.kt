package com.example.edustream.features.progress.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.progress.data.local.entities.LectureCompletionEntity
import com.example.edustream.features.progress.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseProgressUiState(
    val overallProgress: Float = 0f,
    val completedLectures: List<LectureCompletionEntity> = emptyList(),
    val totalLectures: Int = 0
)

@HiltViewModel
class CourseProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(CourseProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            combine(
                progressRepository.getCourseProgress(courseId),
                progressRepository.getCompletedLectures(courseId)
            ) { progress, completed ->
                _uiState.value.copy(
                    overallProgress = progress,
                    completedLectures = completed
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun markLectureAsCompleted(lectureId: String, watchPercent: Float) {
        viewModelScope.launch {
            progressRepository.markLectureCompleted(lectureId, courseId, watchPercent)
        }
    }
}
