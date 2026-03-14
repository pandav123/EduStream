package com.example.edustream.features.marketplace.domain.repository

import androidx.paging.PagingData
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(category: String? = null): Flow<PagingData<CourseEntity>>
    fun getCourseDetail(courseId: String): Flow<CourseEntity?>
    fun getLectures(courseId: String): Flow<List<LectureEntity>>
    fun getPurchasedCourses(): Flow<List<CourseEntity>>
    fun searchCourses(query: String, category: String? = null): Flow<PagingData<CourseEntity>>
    suspend fun refreshCourses()
    suspend fun refreshLectures(courseId: String)
    suspend fun purchaseCourse(courseId: String)
}
