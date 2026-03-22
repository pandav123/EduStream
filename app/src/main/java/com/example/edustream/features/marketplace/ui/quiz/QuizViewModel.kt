package com.example.edustream.features.marketplace.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.QuestionEntity
import com.example.edustream.features.marketplace.data.local.entities.QuizEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val quiz: QuizEntity? = null,
    val questions: List<QuestionEntity> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val score: Int = 0,
    val isFinished: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val quizId: String = checkNotNull(savedStateHandle["quizId"])

    private val _uiState = MutableStateFlow<UiState<QuizState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadQuizData()
    }

    private fun loadQuizData() {
        viewModelScope.launch {
            // Since we don't have a getQuizById in repository, we observe quizzes and find it.
            // Using a dummy courseId or empty might not work if the repository filters by it.
            // However, CourseRepositoryImpl doesn't seem to have a global getQuiz.
            // Let's assume the quiz belongs to a course we can find.
            
            // For now, let's just fetch questions first as that's primary.
            repository.getQuestions(quizId).collect { questions ->
                if (questions.isNotEmpty()) {
                    _uiState.value = UiState.Success(QuizState(quiz = null, questions = questions))
                } else {
                    _uiState.value = UiState.Error("No questions found for this quiz")
                }
            }
        }
    }

    fun onOptionSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        if (currentState.selectedOptionIndex != null) return // Already answered

        _uiState.value = UiState.Success(currentState.copy(selectedOptionIndex = index))
    }

    fun onNextClicked() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        
        val newScore = if (currentState.selectedOptionIndex == currentQuestion.correctAnswerIndex) {
            currentState.score + 1
        } else {
            currentState.score
        }

        if (currentState.currentQuestionIndex + 1 < currentState.questions.size) {
            _uiState.value = UiState.Success(
                currentState.copy(
                    currentQuestionIndex = currentState.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    score = newScore
                )
            )
        } else {
            _uiState.value = UiState.Success(
                currentState.copy(
                    score = newScore,
                    isFinished = true
                )
            )
        }
    }
}
