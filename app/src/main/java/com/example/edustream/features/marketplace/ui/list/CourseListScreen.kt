package com.example.edustream.features.marketplace.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.edustream.features.marketplace.ui.list.components.CourseCard

@Composable
fun CourseListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: CourseListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.courses.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Tabs (Simple implementation)
        val categories = listOf("All", "Design", "Development", "Business", "Marketing")
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory ?: "All"),
            edgePadding = 16.dp,
            divider = {}
        ) {
            categories.forEach { category ->
                Tab(
                    selected = (selectedCategory ?: "All") == category,
                    onClick = { viewModel.selectCategory(if (category == "All") null else category) },
                    text = { Text(category) }
                )
            }
        }

        if (pagingItems.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pagingItems.itemCount) { index ->
                    pagingItems[index]?.let { course ->
                        CourseCard(
                            course = course,
                            onClick = onNavigateToDetail
                        )
                    }
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
