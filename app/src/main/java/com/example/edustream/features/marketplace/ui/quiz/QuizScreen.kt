package com.example.edustream.features.marketplace.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edustream.features.marketplace.data.local.entities.QuestionEntity
import com.example.edustream.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onBackClick: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? UiState.Success)?.data?.quiz?.title ?: "Quiz"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is UiState.Success -> {
                    val data = state.data
                    if (data.isFinished) {
                        QuizResultScreen(
                            score = data.score,
                            totalQuestions = data.questions.size,
                            onBackClick = onBackClick
                        )
                    } else {
                        val currentQuestion = data.questions[data.currentQuestionIndex]
                        QuizContent(
                            question = currentQuestion,
                            currentIndex = data.currentQuestionIndex,
                            totalQuestions = data.questions.size,
                            selectedOptionIndex = data.selectedOptionIndex,
                            onOptionSelected = { viewModel.onOptionSelected(it) },
                            onNextClick = { viewModel.onNextClicked() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizContent(
    question: QuestionEntity,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOptionIndex: Int?,
    onOptionSelected: (Int) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Question ${currentIndex + 1} of $totalQuestions",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = selectedOptionIndex == index
            val isCorrect = index == question.correctAnswerIndex
            val showResult = selectedOptionIndex != null

            val borderColor = when {
                showResult && isCorrect -> Color.Green
                showResult && isSelected && !isCorrect -> Color.Red
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outlineVariant
            }

            val containerColor = when {
                showResult && isCorrect -> Color.Green.copy(alpha = 0.1f)
                showResult && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.1f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> Color.Transparent
            }

            OutlinedCard(
                onClick = { onOptionSelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                border = BorderStroke(2.dp, borderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
                enabled = selectedOptionIndex == null
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${'A' + index}.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedOptionIndex != null && question.explanation != null) {
            Text(
                text = "Explanation:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedOptionIndex != null
        ) {
            Text(if (currentIndex + 1 == totalQuestions) "Finish Quiz" else "Next Question")
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    totalQuestions: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.Green,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Score",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "$score / $totalQuestions",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        val percentage = (score.toFloat() / totalQuestions * 100).toInt()
        Text(
            text = "You scored $percentage%",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Course")
        }
    }
}
