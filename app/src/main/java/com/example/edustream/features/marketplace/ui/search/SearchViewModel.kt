package com.example.edustream.features.marketplace.ui.search

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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<CourseEntity>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            repository.searchCourses(query)
        }
        .cachedIn(viewModelScope)

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
