package com.example.edustream.features.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {
    @Query("SELECT * FROM lecture_table WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getLecturesByCourseId(courseId: String): Flow<List<LectureEntity>>

    @Query("SELECT * FROM lecture_table WHERE lectureId = :lectureId")
    suspend fun getLectureById(lectureId: String): LectureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lectures: List<LectureEntity>)

    @Query("DELETE FROM lecture_table WHERE courseId = :courseId")
    suspend fun deleteLecturesByCourseId(courseId: String)
}
