package com.example.edustream.features.admin.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val courses: StateFlow<List<CourseEntity>> = _courses.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            firestore.collection("courses")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    val courseList = snapshot?.documents?.mapNotNull { doc ->
                        CourseEntity(
                            courseId = doc.getString("courseId") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            instructorId = "admin",
                            thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                            previewVideoUrl = "",
                            price = doc.getDouble("price") ?: 0.0,
                            discountPrice = null,
                            category = doc.getString("category") ?: "",
                            rating = 0f,
                            enrollmentCount = 0,
                            totalLectures = 0,
                            totalDuration = 0,
                            isPurchased = false,
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    } ?: emptyList()
                    _courses.value = courseList
                }
        }
    }

    suspend fun addCourse(
        title: String,
        description: String,
        price: Double,
        category: String,
        thumbnailUrl: String
    ) {
        val courseId = UUID.randomUUID().toString()
        val courseData = mapOf(
            "courseId" to courseId,
            "title" to title,
            "description" to description,
            "price" to price,
            "category" to category,
            "thumbnailUrl" to thumbnailUrl,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("courses").document(courseId).set(courseData).await()
    }

    suspend fun addLecture(courseId: String, title: String, videoUrl: String, orderIndex: Int) {
        val lectureId = UUID.randomUUID().toString()
        val lectureData = mapOf(
            "lectureId" to lectureId,
            "courseId" to courseId,
            "title" to title,
            "videoUrl" to videoUrl,
            "orderIndex" to orderIndex,
            "description" to "Lecture content"
        )
        firestore.collection("courses").document(courseId)
            .collection("lectures").document(lectureId).set(lectureData).await()
    }

    suspend fun uploadPdf(uri: Uri): String {
        val fileName = "notes/${UUID.randomUUID()}.pdf"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun addNote(courseId: String, title: String, pdfUri: Uri) {
        val pdfUrl = uploadPdf(pdfUri)
        val noteId = UUID.randomUUID().toString()
        val noteData = mapOf(
            "noteId" to noteId,
            "courseId" to courseId,
            "title" to title,
            "pdfUrl" to pdfUrl,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("courses").document(courseId)
            .collection("notes").document(noteId).set(noteData).await()
    }

    suspend fun addQuiz(courseId: String, title: String, questions: List<QuestionData>) {
        val quizId = UUID.randomUUID().toString()
        val quizData = mapOf(
            "quizId" to quizId,
            "courseId" to courseId,
            "title" to title,
            "totalQuestions" to questions.size
        )
        firestore.collection("courses").document(courseId)
            .collection("quizzes").document(quizId).set(quizData).await()
            
        questions.forEach { q ->
            val questionId = UUID.randomUUID().toString()
            val questionData = mapOf(
                "questionId" to questionId,
                "quizId" to quizId,
                "questionText" to q.text,
                "options" to q.options,
                "correctAnswerIndex" to q.correctIndex,
                "explanation" to q.explanation
            )
            firestore.collection("courses").document(courseId)
                .collection("quizzes").document(quizId)
                .collection("questions").document(questionId).set(questionData).await()
        }
    }
}

data class QuestionData(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
