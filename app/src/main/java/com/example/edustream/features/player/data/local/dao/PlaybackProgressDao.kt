package com.example.edustream.features.player.data.local.dao

import androidx.room.*
import com.example.edustream.features.player.data.local.entities.PlaybackProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackProgressDao {
    @Query("SELECT * FROM playback_progress_table WHERE lectureId = :lectureId")
    fun getProgressByLecture(lectureId: String): Flow<PlaybackProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: PlaybackProgressEntity)

    @Query("SELECT * FROM playback_progress_table WHERE isCompleted = 0 ORDER BY lastUpdated DESC")
    fun getInProgressCourses(): Flow<List<PlaybackProgressEntity>>
}
