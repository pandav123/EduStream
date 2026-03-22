package com.example.edustream.features.marketplace.domain.repository

import androidx.paging.PagingData
import com.example.edustream.features.marketplace.data.local.entities.*
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(category: String? = null, query: String? = null): Flow<PagingData<CourseEntity>>
    fun getCourseDetail(courseId: String): Flow<CourseEntity?>
    fun getLectures(courseId: String): Flow<List<LectureEntity>>
    fun getQuizzes(courseId: String): Flow<List<QuizEntity>>
    fun getQuestions(quizId: String): Flow<List<QuestionEntity>>
    fun getNotes(courseId: String): Flow<List<NoteEntity>>
    fun getPurchasedCourses(): Flow<List<CourseEntity>>
    fun searchCourses(query: String, category: String? = null): Flow<PagingData<CourseEntity>>
    suspend fun refreshCourses()
    suspend fun refreshLectures(courseId: String)
    suspend fun refreshQuizzes(courseId: String)
    suspend fun refreshNotes(courseId: String)
    suspend fun purchaseCourse(courseId: String)
}
