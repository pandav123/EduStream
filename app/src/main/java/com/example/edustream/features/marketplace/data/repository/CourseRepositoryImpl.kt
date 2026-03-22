package com.example.edustream.features.marketplace.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.edustream.features.marketplace.data.local.dao.*
import com.example.edustream.features.marketplace.data.local.entities.*
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val lectureDao: LectureDao,
    private val quizDao: QuizDao,
    private val noteDao: NoteDao,
    private val firestore: FirebaseFirestore
) : CourseRepository {

    override fun getCourses(category: String?, query: String?): Flow<PagingData<CourseEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { 
                if (category == null) courseDao.getAllCourses(query) 
                else courseDao.getCoursesByCategory(category, query)
            }
        ).flow
    }

    override fun getCourseDetail(courseId: String): Flow<CourseEntity?> {
        return courseDao.getCourseById(courseId)
    }

    override fun getLectures(courseId: String): Flow<List<LectureEntity>> {
        return lectureDao.getLecturesByCourseId(courseId)
    }

    override fun getQuizzes(courseId: String): Flow<List<QuizEntity>> {
        return quizDao.getQuizzesByCourseId(courseId)
    }

    override fun getQuestions(quizId: String): Flow<List<QuestionEntity>> {
        return quizDao.getQuestionsByQuizId(quizId)
    }

    override fun getNotes(courseId: String): Flow<List<NoteEntity>> {
        return noteDao.getNotesByCourseId(courseId)
    }

    override fun getPurchasedCourses(): Flow<List<CourseEntity>> {
        return courseDao.getPurchasedCourses()
    }

    override fun searchCourses(query: String, category: String?): Flow<PagingData<CourseEntity>> {
        return getCourses(category, query)
    }

    override suspend fun refreshCourses() {
        try {
            val snapshot = firestore.collection("courses").get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val title = doc.getString("title") ?: ""
                val description = doc.getString("description") ?: ""
                val instructorId = doc.getString("instructorId") ?: ""
                val thumbnailUrl = doc.getString("thumbnailUrl") ?: ""
                val previewVideoUrl = doc.getString("previewVideoUrl") ?: ""
                val price = doc.getDouble("price") ?: 0.0
                val discountPrice = doc.getDouble("discountPrice")
                val category = doc.getString("category") ?: ""
                val rating = doc.getDouble("rating")?.toFloat() ?: 0f
                val enrollmentCount = doc.getLong("enrollmentCount")?.toInt() ?: 0
                val totalLectures = doc.getLong("totalLectures")?.toInt() ?: 0
                val totalDuration = doc.getLong("totalDuration") ?: 0L
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                // Check local DB to preserve purchase status
                val existing = courseDao.getCourseById(id).firstOrNull()
                val isPurchased = existing?.isPurchased ?: false

                CourseEntity(
                    courseId = id,
                    title = title,
                    description = description,
                    instructorId = instructorId,
                    thumbnailUrl = thumbnailUrl,
                    previewVideoUrl = previewVideoUrl,
                    price = price,
                    discountPrice = discountPrice,
                    category = category,
                    rating = rating,
                    enrollmentCount = enrollmentCount,
                    totalLectures = totalLectures,
                    totalDuration = totalDuration,
                    isPurchased = isPurchased,
                    createdAt = createdAt
                )
            }
            courseDao.insertAll(entities)
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun refreshLectures(courseId: String) {
        try {
            val snapshot = firestore.collection("courses").document(courseId).collection("lectures").get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
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
            }
            lectureDao.insertAll(entities)
        } catch (e: Exception) {}
    }

    override suspend fun refreshQuizzes(courseId: String) {
        try {
            val snapshot = firestore.collection("courses").document(courseId).collection("quizzes").get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                QuizEntity(
                    quizId = doc.id,
                    courseId = courseId,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 0,
                    orderIndex = doc.getLong("orderIndex")?.toInt() ?: 0
                )
            }
            quizDao.insertQuizzes(entities)
            
            entities.forEach { quiz ->
                refreshQuestions(quiz.quizId, courseId)
            }
        } catch (e: Exception) {}
    }

    private suspend fun refreshQuestions(quizId: String, courseId: String) {
        try {
            val snapshot = firestore.collection("courses").document(courseId)
                .collection("quizzes").document(quizId).collection("questions").get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                @Suppress("UNCHECKED_CAST")
                QuestionEntity(
                    questionId = doc.id,
                    quizId = quizId,
                    questionText = doc.getString("questionText") ?: "",
                    options = doc.get("options") as? List<String> ?: emptyList(),
                    correctAnswerIndex = doc.getLong("correctAnswerIndex")?.toInt() ?: 0,
                    explanation = doc.getString("explanation") ?: ""
                )
            }
            quizDao.insertQuestions(entities)
        } catch (e: Exception) {}
    }

    override suspend fun refreshNotes(courseId: String) {
        try {
            // Fetching from top-level "pdfs" collection as requested
            val snapshot = firestore.collection("pdfs")
                .whereEqualTo("courseId", courseId)
                .get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                NoteEntity(
                    noteId = doc.getString("id") ?: doc.id,
                    courseId = doc.getString("courseId") ?: courseId,
                    title = doc.getString("name") ?: "",
                    pdfUrl = doc.getString("url") ?: "",
                    fileSize = "",
                    createdAt = doc.getLong("uploadedAt") ?: System.currentTimeMillis()
                )
            }
            noteDao.insertAll(entities)
        } catch (e: Exception) {}
    }

    override suspend fun purchaseCourse(courseId: String) {
        courseDao.markAsPurchased(courseId)
    }
}
