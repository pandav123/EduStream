package com.example.edustream.features.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edustream.features.marketplace.ui.list.components.CourseCard
import com.example.edustream.features.player.data.local.entities.PlaybackProgressEntity
import com.example.edustream.ui.common.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onNavigateToCourseList: (String?) -> Unit,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToPlayer: (String, String) -> Unit, // videoUrl placeholder, lectureId
    viewModel: HomeViewModel = hiltViewModel()
) {
    val featuredCoursesState by viewModel.featuredCourses.collectAsState()
    val inProgressCourses by viewModel.inProgressCourses.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Continue Watching Section
        if (inProgressCourses.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Continue Watching",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(inProgressCourses) { progress ->
                            ContinueWatchingCard(
                                progress = progress,
                                onClick = { onNavigateToPlayer("", progress.lectureId) }
                            )
                        }
                    }
                }
            }
        }

        // Featured Courses Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Featured Courses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                when (val state = featuredCoursesState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            Text("Check back later for featured content", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(state.data) { course ->
                                    CourseCard(
                                        course = course,
                                        onClick = onNavigateToCourseDetail,
                                        modifier = Modifier.width(280.dp)
                                    )
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text("Error loading featured courses", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Categories Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Browse Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                val categories = listOf("Design", "Development", "Business", "Marketing", "Music")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToCourseList(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    progress: PlaybackProgressEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Lecture ID: ${progress.lectureId}",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            val progressPercent = if (progress.totalDuration > 0) {
                progress.lastPosition.toFloat() / progress.totalDuration
            } else 0f
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${(progressPercent * 100).toInt()}% completed",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
