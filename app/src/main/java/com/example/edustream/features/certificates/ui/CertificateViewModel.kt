package com.example.edustream.features.certificates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.features.progress.domain.repository.ProgressRepository
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _completedCourses = MutableStateFlow<UiState<List<CourseEntity>>>(UiState.Loading)
    val completedCourses = _completedCourses.asStateFlow()

    init {
        loadCompletedCourses()
    }

    private fun loadCompletedCourses() {
        viewModelScope.launch {
            // This is a simplified logic: 
            // 1. Get all courses user has interacted with
            // 2. Filter those where progress is 100%
            // In a real app, you'd probably have an 'enrolled_courses' table or API.
            
            // Mocking for now: we'll get all courses and check progress
            courseRepository.getCourses().map { pagingData ->
                // This is tricky with PagingData. 
                // For certificates, we ideally want a non-paginated list of COMPLETED courses.
                emptyList<CourseEntity>() 
            }.collectLatest {
                // _completedCourses.value = UiState.Success(it)
            }
            
            // Let's just provide a Success empty list for now to unblock UI
            _completedCourses.value = UiState.Success(emptyList())
        }
    }
    
    fun downloadCertificate(courseId: String) {
        // TODO: Implement PDF download logic (via API)
    }
}
