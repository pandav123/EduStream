package com.example.edustream.features.admin.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.auth.domain.repository.AuthRepository
import com.example.edustream.features.marketplace.data.local.entities.*
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val courses: StateFlow<List<CourseEntity>> = _courses.asStateFlow()

    private val _currentCourseLectures = MutableStateFlow<List<LectureEntity>>(emptyList())
    val currentCourseLectures = _currentCourseLectures.asStateFlow()

    private val _currentCourseNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val currentCourseNotes = _currentCourseNotes.asStateFlow()

    private val _currentCourseQuizzes = MutableStateFlow<List<QuizEntity>>(emptyList())
    val currentCourseQuizzes = _currentCourseQuizzes.asStateFlow()

    private var lecturesListener: ListenerRegistration? = null
    private var notesListener: ListenerRegistration? = null
    private var quizzesListener: ListenerRegistration? = null

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            val adminId = authRepository.currentUser.first()?.uid ?: return@launch
            
            firestore.collection("courses")
                .whereEqualTo("instructorId", adminId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    val courseList = snapshot?.documents?.mapNotNull { doc ->
                        CourseEntity(
                            courseId = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            instructorId = doc.getString("instructorId") ?: "",
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

    fun loadCourseContent(courseId: String) {
        lecturesListener?.remove()
        notesListener?.remove()
        quizzesListener?.remove()

        // Load Lectures
        lecturesListener = firestore.collection("courses").document(courseId).collection("lectures")
            .addSnapshotListener { snapshot, _ ->
                _currentCourseLectures.value = snapshot?.documents?.mapNotNull { doc ->
                    LectureEntity(
                        lectureId = doc.id,
                        courseId = courseId,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        videoUrl = doc.getString("videoUrl") ?: "",
                        duration = doc.getLong("duration") ?: 0L,
                        orderIndex = doc.getLong("orderIndex")?.toInt() ?: 0,
                        isPreview = doc.getBoolean("isPreview") ?: false
                    )
                } ?: emptyList()
            }

        // Load Notes from top-level "pdfs" collection
        notesListener = firestore.collection("pdfs")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, _ ->
                _currentCourseNotes.value = snapshot?.documents?.mapNotNull { doc ->
                    NoteEntity(
                        noteId = doc.getString("id") ?: doc.id,
                        courseId = doc.getString("courseId") ?: courseId,
                        title = doc.getString("name") ?: "",
                        pdfUrl = doc.getString("url") ?: "",
                        fileSize = "",
                        createdAt = doc.getLong("uploadedAt") ?: 0L
                    )
                } ?: emptyList()
            }

        // Load Quizzes
        quizzesListener = firestore.collection("courses").document(courseId).collection("quizzes")
            .addSnapshotListener { snapshot, _ ->
                _currentCourseQuizzes.value = snapshot?.documents?.mapNotNull { doc ->
                    QuizEntity(
                        quizId = doc.id,
                        courseId = courseId,
                        title = doc.getString("title") ?: "",
                        description = "",
                        totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 0,
                        orderIndex = 0
                    )
                } ?: emptyList()
            }
    }

    override fun onCleared() {
        super.onCleared()
        lecturesListener?.remove()
        notesListener?.remove()
        quizzesListener?.remove()
    }

    suspend fun addCourse(
        title: String,
        description: String,
        price: Double,
        category: String,
        thumbnailUrl: String
    ) {
        val adminId = authRepository.currentUser.first()?.uid ?: return
        val courseId = UUID.randomUUID().toString()
        val courseData = mapOf(
            "courseId" to courseId,
            "title" to title,
            "description" to description,
            "price" to price,
            "category" to category,
            "instructorId" to adminId,
            "thumbnailUrl" to thumbnailUrl,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("courses").document(courseId).set(courseData).await()
    }

    suspend fun deleteCourse(courseId: String) {
        firestore.collection("courses").document(courseId).delete().await()
    }

    suspend fun deleteLecture(courseId: String, lectureId: String) {
        firestore.collection("courses").document(courseId)
            .collection("lectures").document(lectureId).delete().await()
    }

    suspend fun deleteNote(courseId: String, noteId: String) {
        firestore.collection("pdfs").document(noteId).delete().await()
    }

    suspend fun deleteQuiz(courseId: String, quizId: String) {
        firestore.collection("courses").document(courseId)
            .collection("quizzes").document(quizId).delete().await()
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

    suspend fun uploadPdfAndAddNote(courseId: String, title: String, pdfUri: Uri) {
        val timestamp = System.currentTimeMillis()
        val originalFileName = getFileName(pdfUri)?.replace(Regex("[^a-zA-Z0-9.]"), "_") ?: "document.pdf"
        val storagePath = "pdfs/${timestamp}_$originalFileName"
        val storageRef = storage.reference.child(storagePath)
        
        // Use putFile for better reliability with content Uris
        storageRef.putFile(pdfUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        val noteId = UUID.randomUUID().toString()
        val noteData = mapOf(
            "id" to noteId,
            "courseId" to courseId,
            "name" to title,
            "url" to downloadUrl,
            "uploadedAt" to timestamp
        )
        firestore.collection("pdfs").document(noteId).set(noteData).await()
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
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
