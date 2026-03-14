package com.example.edustream.features.marketplace.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class FilterState(
    val category: String? = null,
    val minRating: Float? = null,
    val sortBy: String = "Popular"
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilters = MutableStateFlow(FilterState())
    val activeFilters = _activeFilters.asStateFlow()

    val searchResults: Flow<PagingData<CourseEntity>> = combine(
        _searchQuery.debounce(300),
        _activeFilters
    ) { query, filters ->
        Pair(query, filters)
    }.flatMapLatest { (query, filters) ->
        repository.getCourses(filters.category)
    }.cachedIn(viewModelScope)

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun applyFilters(filters: FilterState) {
        _activeFilters.value = filters
    }

    fun clearFilters() {
        _activeFilters.value = FilterState()
    }
}
