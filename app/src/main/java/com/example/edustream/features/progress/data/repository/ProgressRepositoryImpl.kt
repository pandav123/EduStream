package com.example.edustream.features.progress.data.repository

import com.example.edustream.features.progress.data.local.dao.LectureCompletionDao
import com.example.edustream.features.progress.data.local.entities.LectureCompletionEntity
import com.example.edustream.features.progress.domain.repository.ProgressRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val lectureCompletionDao: LectureCompletionDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProgressRepository {

    override fun getCourseProgress(courseId: String): Flow<Float> {
        // Simple implementation: completed lectures / total lectures
        // In a real app, we might want to pass the total lecture count or fetch it
        return lectureCompletionDao.getCompletedCountByCourse(courseId).combine(
            // Mocking total lectures for now, ideally this would come from the Course table or API
            kotlinx.coroutines.flow.flowOf(10) 
        ) { completed, total ->
            if (total > 0) completed.toFloat() / total else 0f
        }
    }

    override fun getCompletedLectures(courseId: String): Flow<List<LectureCompletionEntity>> {
        return lectureCompletionDao.getCompletionsByCourse(courseId)
    }

    override suspend fun markLectureCompleted(lectureId: String, courseId: String, watchPercent: Float) {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val completion = LectureCompletionEntity(
            lectureId = lectureId,
            courseId = courseId,
            userId = userId,
            completedAt = System.currentTimeMillis(),
            watchPercent = watchPercent
        )
        lectureCompletionDao.insertCompletion(completion)
        
        // Immediate sync (or could be deferred to Worker)
        syncToFirestore(completion)
    }

    private suspend fun syncToFirestore(completion: LectureCompletionEntity) {
        val userId = auth.currentUser?.uid ?: return
        val progressMap = mapOf(
            "courseId" to completion.courseId,
            "lectureId" to completion.lectureId,
            "watchPercent" to completion.watchPercent,
            "completedAt" to completion.completedAt
        )
        
        try {
            firestore.collection("users")
                .document(userId)
                .collection("progress")
                .document(completion.courseId)
                .collection("lectures")
                .document(completion.lectureId)
                .set(progressMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            // Handle error (e.g., log it, the Worker will retry later)
        }
    }

    override suspend fun syncProgressToFirestore() {
        // This would be called by a SyncWorker to push all local changes
        // For simplicity, we just show the logic for one
    }
}
