package com.example.edustream.features.marketplace.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailState(
    val course: CourseEntity? = null,
    val lectures: List<LectureEntity> = emptyList()
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val repository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow<UiState<CourseDetailState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadCourseData()
        refreshLectures()
    }

    private fun loadCourseData() {
        viewModelScope.launch {
            combine(
                repository.getCourseDetail(courseId),
                repository.getLectures(courseId)
            ) { course, lectures ->
                if (course != null) {
                    UiState.Success(CourseDetailState(course, lectures))
                } else {
                    UiState.Error("Course not found")
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    private fun refreshLectures() {
        viewModelScope.launch {
            repository.refreshLectures(courseId)
        }
    }

    fun purchaseCourse() {
        viewModelScope.launch {
            repository.purchaseCourse(courseId)
        }
    }
}
