package com.example.edustream.features.progress.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseProgressScreen(
    onBackClick: () -> Unit,
    onLectureClick: (String, String) -> Unit,
    viewModel: CourseProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Progress") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Overall Progress Header
            CourseProgressHeader(progress = uiState.overallProgress)
            
            HorizontalDivider()

            Text(
                text = "Curriculum",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            // In a real app, lectures would be in the uiState
            val mockLectures = listOf<LectureEntity>()

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mockLectures) { lecture ->
                    val isCompleted = uiState.completedLectures.any { it.lectureId == lecture.lectureId }
                    LectureProgressItem(
                        lecture = lecture,
                        isCompleted = isCompleted,
                        onClick = { onLectureClick(lecture.courseId, lecture.lectureId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseProgressHeader(progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = "Overall Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            val message = if (progress >= 1.0f) "Course Completed! 🎉" else "Keep going!"
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LectureProgressItem(
    lecture: LectureEntity,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(lecture.title) },
        supportingContent = { Text("${lecture.duration / 60} min") },
        leadingContent = {
            if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
}
