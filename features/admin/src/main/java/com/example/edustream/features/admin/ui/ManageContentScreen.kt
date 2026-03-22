package com.example.edustream.features.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageContentScreen(
    courseId: String,
    onBackClick: () -> Unit,
    onAddQuizClick: (String) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()Easy
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showLectureDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Content") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Course ID: $courseId", style = MaterialTheme.typography.bodySmall)

            ContentActionCard(
                title = "Add Lecture",
                description = "Upload video content and titles",
                icon = Icons.Default.VideoLibrary,
                onClick = { showLectureDialog = true }
            )

            ContentActionCard(
                title = "Add Study Note",
                description = "Upload PDF notes for students",
                icon = Icons.Default.Description,
                onClick = { showNoteDialog = true }
            )

            ContentActionCard(
                title = "Create Quiz",
                description = "Add interactive questions and answers",
                icon = Icons.Default.Quiz,
                onClick = { onAddQuizClick(courseId) }
            )
        }
    }

    if (showLectureDialog) {
        AddLectureDialog(
            onDismiss = { showLectureDialog = false },
            onConfirm = { title, url ->
                scope.launch {
                    viewModel.addLecture(courseId, title, url, 0)
                    snackbarHostState.showSnackbar("Lecture added successfully")
                    showLectureDialog = false
                }
            }
        )
    }

    if (showNoteDialog) {
        AddNoteDialog(
            onDismiss = { showNoteDialog = false },
            onConfirm = { title, url ->
                scope.launch {
                    viewModel.addNote(courseId, title, url)
                    snackbarHostState.showSnackbar("Note added successfully")
                    showNoteDialog = false
                }
            }
        )
    }
}

@Composable
fun ContentActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        ListItem(
            headlineContent = { Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            supportingContent = { Text(description) },
            leadingContent = { Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp)) },
            trailingContent = { Icon(Icons.Default.Add, contentDescription = null) }
        )
    }
}

@Composable
fun AddLectureDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Lecture") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Lecture Title") })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Video URL") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, url) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Study Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Note Title") })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("PDF URL") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, url) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
