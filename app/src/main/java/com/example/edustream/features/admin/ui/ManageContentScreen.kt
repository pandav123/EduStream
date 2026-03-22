package com.example.edustream.features.admin.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    viewModel: AdminViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val lectures by viewModel.currentCourseLectures.collectAsState()
    val notes by viewModel.currentCourseNotes.collectAsState()
    val quizzes by viewModel.currentCourseQuizzes.collectAsState()

    var showLectureDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    
    var itemToDelete by remember { mutableStateOf<DeleteTarget?>(null) }

    LaunchedEffect(courseId) {
        viewModel.loadCourseContent(courseId)
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete ${itemToDelete?.type}") },
            text = { Text("Are you sure you want to delete this ${itemToDelete?.type?.lowercase()}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            when (val target = itemToDelete) {
                                is DeleteTarget.Lecture -> viewModel.deleteLecture(courseId, target.id)
                                is DeleteTarget.Note -> viewModel.deleteNote(courseId, target.id)
                                is DeleteTarget.Quiz -> viewModel.deleteQuiz(courseId, target.id)
                                null -> {}
                            }
                            snackbarHostState.showSnackbar("${itemToDelete?.type} deleted")
                            itemToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "Course ID: $courseId", style = MaterialTheme.typography.bodySmall)
            }

            item {
                Text("Actions", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showLectureDialog = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Lecture")
                    }
                    Button(onClick = { showNoteDialog = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Description, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Note")
                    }
                    Button(onClick = { onAddQuizClick(courseId) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Quiz, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Quiz")
                    }
                }
            }

            if (lectures.isNotEmpty()) {
                item { Text("Lectures", style = MaterialTheme.typography.titleMedium) }
                items(lectures) { lecture ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(lecture.title) },
                            leadingContent = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                            trailingContent = {
                                IconButton(onClick = { itemToDelete = DeleteTarget.Lecture(lecture.lectureId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }

            if (notes.isNotEmpty()) {
                item { Text("Study Notes", style = MaterialTheme.typography.titleMedium) }
                items(notes) { note ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(note.title) },
                            leadingContent = { Icon(Icons.Default.Description, contentDescription = null) },
                            trailingContent = {
                                IconButton(onClick = { itemToDelete = DeleteTarget.Note(note.noteId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }

            if (quizzes.isNotEmpty()) {
                item { Text("Quizzes", style = MaterialTheme.typography.titleMedium) }
                items(quizzes) { quiz ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(quiz.title) },
                            supportingContent = { Text("${quiz.totalQuestions} Questions") },
                            leadingContent = { Icon(Icons.Default.Quiz, contentDescription = null) },
                            trailingContent = {
                                IconButton(onClick = { itemToDelete = DeleteTarget.Quiz(quiz.quizId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }
            
            if (lectures.isEmpty() && notes.isEmpty() && quizzes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No content added yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    if (showLectureDialog) {
        AddLectureDialog(
            onDismiss = { showLectureDialog = false },
            onConfirm = { title, url ->
                scope.launch {
                    try {
                        viewModel.addLecture(courseId, title, url, lectures.size)
                        snackbarHostState.showSnackbar("Lecture added successfully")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to add lecture: ${e.message}")
                    } finally {
                        showLectureDialog = false
                    }
                }
            }
        )
    }

    if (showNoteDialog) {
        AddNoteDialog(
            onDismiss = { showNoteDialog = false },
            onConfirm = { title, pdfUri ->
                scope.launch {
                    try {
                        viewModel.uploadPdfAndAddNote(courseId, title, pdfUri)
                        snackbarHostState.showSnackbar("Note uploaded successfully")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to upload: ${e.message}")
                    } finally {
                        showNoteDialog = false
                    }
                }
            }
        )
    }
}

sealed class DeleteTarget(val id: String, val type: String) {
    class Lecture(id: String) : DeleteTarget(id, "Lecture")
    class Note(id: String) : DeleteTarget(id, "Note")
    class Quiz(id: String) : DeleteTarget(id, "Quiz")
}

@Composable
fun AddLectureDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = { Text("New Lecture") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Lecture Title") }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = url, 
                    onValueChange = { url = it }, 
                    label = { Text("Video URL") }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    isLoading = true
                    onConfirm(title, url) 
                }, 
                enabled = title.isNotBlank() && url.isNotBlank() && !isLoading
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Uri) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPdfUri = uri
    }

    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = { Text("Upload Study Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (selectedPdfUri != null) {
                    Text(
                        text = "Selected: ${selectedPdfUri?.path?.split("/")?.lastOrNull() ?: "PDF File"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Button(
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(if (selectedPdfUri == null) "Select PDF" else "Change PDF")
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    isLoading = true
                    selectedPdfUri?.let { onConfirm(title, it) } 
                },
                enabled = title.isNotBlank() && selectedPdfUri != null && !isLoading
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )
}
