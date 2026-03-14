package com.example.edustream.features.marketplace.ui.mycourses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCoursesViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _purchasedCourses = MutableStateFlow<UiState<List<CourseEntity>>>(UiState.Loading)
    val purchasedCourses = _purchasedCourses.asStateFlow()

    init {
        loadPurchasedCourses()
    }

    fun loadPurchasedCourses() {
        viewModelScope.launch {
            _purchasedCourses.value = UiState.Loading
            repository.getPurchasedCourses().collectLatest {
                _purchasedCourses.value = UiState.Success(it)
            }
        }
    }
}
