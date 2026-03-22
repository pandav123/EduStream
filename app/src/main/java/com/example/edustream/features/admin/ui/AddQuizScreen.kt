package com.example.edustream.features.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuizScreen(
    courseId: String,
    onBackClick: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var quizTitle by remember { mutableStateOf("") }
    val questions = remember { mutableStateListOf<QuestionData>() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (quizTitle.isBlank() || questions.isEmpty()) {
                            scope.launch { snackbarHostState.showSnackbar("Add title and at least one question") }
                            return@TextButton
                        }
                        scope.launch {
                            viewModel.addQuiz(courseId, quizTitle, questions.toList())
                            snackbarHostState.showSnackbar("Quiz created successfully")
                            onBackClick()
                        }
                    }) {
                        Text("SAVE")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                questions.add(QuestionData("", listOf("", "", "", ""), 0, ""))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Question")
            }
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
                OutlinedTextField(
                    value = quizTitle,
                    onValueChange = { quizTitle = it },
                    label = { Text("Quiz Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            itemsIndexed(questions) { index, question ->
                QuestionEditor(
                    index = index,
                    question = question,
                    onQuestionChange = { updated -> questions[index] = updated },
                    onDelete = { questions.removeAt(index) }
                )
            }
        }
    }
}

@Composable
fun QuestionEditor(
    index: Int,
    question: QuestionData,
    onQuestionChange: (QuestionData) -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Question ${index + 1}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            OutlinedTextField(
                value = question.text,
                onValueChange = { onQuestionChange(question.copy(text = it)) },
                label = { Text("Question Text") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "Options", style = MaterialTheme.typography.labelLarge)
            question.options.forEachIndexed { optIndex, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = question.correctIndex == optIndex,
                        onClick = { onQuestionChange(question.copy(correctIndex = optIndex)) }
                    )
                    OutlinedTextField(
                        value = option,
                        onValueChange = { newText ->
                            val newOptions = question.options.toMutableList()
                            newOptions[optIndex] = newText
                            onQuestionChange(question.copy(options = newOptions))
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Option ${optIndex + 1}") }
                    )
                }
            }

            OutlinedTextField(
                value = question.explanation,
                onValueChange = { onQuestionChange(question.copy(explanation = it)) },
                label = { Text("Explanation (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
