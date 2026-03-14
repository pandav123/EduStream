package com.example.edustream.features.marketplace.ui.mycourses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edustream.features.marketplace.ui.list.components.CourseCard
import com.example.edustream.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: MyCoursesViewModel = hiltViewModel()
) {
    val purchasedCoursesState by viewModel.purchasedCourses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Courses") })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = purchasedCoursesState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text("You haven't enrolled in any courses yet.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data) { course ->
                                CourseCard(
                                    course = course,
                                    onClick = onNavigateToDetail
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
