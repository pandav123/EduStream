package com.example.edustream.features.marketplace.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val courses: Flow<PagingData<CourseEntity>> = combine(
        _selectedCategory,
        _searchQuery.debounce(300L)
    ) { category, query ->
        category to query
    }.flatMapLatest { (category, query) ->
        repository.getCourses(category, query)
    }.onStart {
        repository.refreshCourses() // Ensure dummy data is present
    }.cachedIn(viewModelScope)

    init {
        refreshCourses()
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refreshCourses() {
        viewModelScope.launch {
            repository.refreshCourses()
        }
    }
}
