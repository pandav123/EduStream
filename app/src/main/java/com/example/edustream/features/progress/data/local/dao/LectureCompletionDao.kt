package com.example.edustream.features.progress.data.local.dao

import androidx.room.*
import com.example.edustream.features.progress.data.local.entities.LectureCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureCompletionDao {
    @Query("SELECT * FROM lecture_completion_table WHERE courseId = :courseId")
    fun getCompletionsByCourse(courseId: String): Flow<List<LectureCompletionEntity>>

    @Query("SELECT * FROM lecture_completion_table WHERE lectureId = :lectureId")
    suspend fun getCompletionByLecture(lectureId: String): LectureCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: LectureCompletionEntity)

    @Query("SELECT COUNT(*) FROM lecture_completion_table WHERE courseId = :courseId")
    fun getCompletedCountByCourse(courseId: String): Flow<Int>

    @Query("DELETE FROM lecture_completion_table")
    suspend fun clearAll()
}
