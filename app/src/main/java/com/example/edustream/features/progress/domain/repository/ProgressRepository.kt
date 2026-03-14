package com.example.edustream.features.progress.domain.repository

import com.example.edustream.features.progress.data.local.entities.LectureCompletionEntity
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getCourseProgress(courseId: String): Flow<Float>
    fun getCompletedLectures(courseId: String): Flow<List<LectureCompletionEntity>>
    suspend fun markLectureCompleted(lectureId: String, courseId: String, watchPercent: Float)
    suspend fun syncProgressToFirestore()
}
